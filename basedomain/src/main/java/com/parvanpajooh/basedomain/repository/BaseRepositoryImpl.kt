package com.parvanpajooh.basedomain.repository

import com.parvanpajooh.basedomain.models.request.LoginReq
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import com.parvanpajooh.basedomain.utils.username
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.launchIO
import dev.kourosh.basedomain.whenUnAuthorized

abstract class BaseRepositoryImpl(
        private val appContract: BaseAppModuleRepository,
        private val dataContract: BaseDataModuleRepository,
        private val deviceContract: BaseDeviceModuleRepository
) : BaseRepository {

    private suspend fun getToken(): Result<String> {
        return deviceContract.getToken(username)
                .substitute({ token ->
                    Result.Success(token)
                }, { message, code ->
                    when (code) {
                        ErrorCode.TOKEN_EXPIRED -> {
                            dataContract.getTokenWithRefreshToken(
                                    deviceContract.getRefreshToken(username)
                            ).substitute({ data ->
                                deviceContract.updateAccount(username, data)
                                Result.Success("${data.tokenType} ${data.accessToken}")
                            }, { message1, code1 ->
                                when (code1) {
                                    ErrorCode.NETWORK_ERROR -> {
                                        Result.Success("NetworkError")
                                    }
                                    else -> {
                                        invalidateToken()
                                        Result.Error(message1, code1)
                                    }

                                }
                            })

                        }
                        ErrorCode.UNAVAILABLE_ACCOUNT -> {
                            invalidateToken()
                            Result.Error(message, code)
                        }
                        else -> {
                            invalidateToken()
                            Result.Error(message, code)
                        }
                    }
                })
    }

    protected suspend fun updateTokenWithRefreshToken(): Result<String> {
        return dataContract.getTokenWithRefreshToken(deviceContract.getRefreshToken(username))
                .map { data ->
                    deviceContract.updateAccount(username, data)
                    "${data.tokenType} ${data.accessToken}"
                }

    }


    protected fun invalidateToken() {
         deviceContract.invalidateToken(username)
    }

    private suspend fun getTokenWithAccount(model: LoginReq): Result<String> {
        return dataContract.getTokenWithAccount(model.username, model.password).map {
            PrefHelper.put(BasePrefKey.USERNAME.name, model.username)
            deviceContract.createAccount(model.username, it)
            model.username
        }
    }

    private suspend fun getTokenWithRefreshToken(username: String): Result<String> {
        return dataContract.getTokenWithRefreshToken(deviceContract.getRefreshToken(username))
                .map {
                    deviceContract.updateAccount(username, it)
                    "${it.tokenType} ${it.accessToken}"
                }
    }

    override suspend fun autoLogin(username: String): Result<Unit> {
        return getToken().whenUnAuthorized { getTokenWithRefreshToken(username) }
                .map { Unit }
    }

    override suspend fun initialize(): Result<Unit> {
        return getToken().bind {
            dataContract.initialize(it)
        }
    }

    override suspend fun login(parameter: LoginReq): Result<Unit> {
        return getTokenWithAccount(parameter).map {
            PrefHelper.put(BasePrefKey.LOGGED_IN.name, true)
            Unit
        }
    }

    override fun logout() {
        PrefHelper.delete(BasePrefKey.LOGGED_IN.name)
        launchIO {
            invalidateToken()
        }
        appContract.goToLogin()


    }

    protected suspend fun <T : Any> getTokenAndRetryWhenUnauthorize(func: suspend (token: String) -> Result<T>): Result<T> {
        return getToken().bind {
            func(it)
        }.whenUnAuthorized {
            updateTokenWithRefreshToken().substitute({ token ->
                func(token).whenUnAuthorized {
                    logout()
                    it
                }
            }, { _, _ ->
                logout()
                it
            })
        }
    }

}
