package com.parvanpajooh.basedomain.repository

import com.parvanpajooh.basedomain.models.request.LoginReq
import com.parvanpajooh.basedomain.utils.findUsername
import com.parvanpajooh.basedomain.utils.findUsernameSuspend
import com.parvanpajooh.basedomain.utils.logUsernameIsNull
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.*

abstract class BaseRepositoryImpl(
    private val appContract: BaseAppModuleRepository,
    private val dataContract: BaseDataModuleRepository,
    private val deviceContract: BaseDeviceModuleRepository
) : BaseRepository {

    private suspend fun getToken(): Result<String> {
        return findUsernameSuspend({ username ->
            deviceContract.getToken(username)
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
        }, {
            logW("getToken")
            Result.Error("unavailable account ", ErrorCode.UNAVAILABLE_ACCOUNT)
        })
    }

    protected suspend fun updateTokenWithRefreshToken(): Result<String> {
        return findUsernameSuspend({ username ->
            dataContract.getTokenWithRefreshToken(deviceContract.getRefreshToken(username))
                .map { data ->
                    deviceContract.updateAccount(username, data)
                    "${data.tokenType} ${data.accessToken}"
                }
        }, {
            logUsernameIsNull("updateTokenWithRefreshToken")
            Result.Error("unavailable account ", ErrorCode.UNAVAILABLE_ACCOUNT) })
    }


    protected fun invalidateToken() {
        findUsername({username -> deviceContract.invalidateToken(username) },{
            logUsernameIsNull("invalidateToken")
        })
    }

    private suspend fun getTokenWithAccount(model: LoginReq): Result<String> {
        return dataContract.getTokenWithAccount(model.username, model.password).map {
            PrefHelper.put(BasePrefKey.USERNAME.name, model.username)
            PrefHelper.put(BasePrefKey.LATEST_USERNAME.name, model.username)
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
        return getTokenWithAccount(parameter).map { Unit }
    }

    override fun logout() {
        launchIO {
            invalidateToken()
        }
        PrefHelper.delete(BasePrefKey.USERNAME.name)
        appContract.goToLogin()


    }

    protected suspend fun <T : Any> getTokenAndWhenUnauthorizeRetry(func: suspend (token: String) -> Result<T>): Result<T> {
        return getToken().bind {
            func(it)
        }.whenUnAuthorized {
            updateTokenWithRefreshToken().substitute({ token ->
                func(token).whenUnAuthorized {
                    PrefHelper.delete(BasePrefKey.USERNAME.name)
                    appContract.goToLogin()
                    it
                }
            }, { _, _ ->
                PrefHelper.delete(BasePrefKey.USERNAME.name)
                appContract.goToLogin()
                it
            })
        }
    }

}
