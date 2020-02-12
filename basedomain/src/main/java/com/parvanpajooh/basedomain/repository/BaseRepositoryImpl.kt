package com.parvanpajooh.basedomain.repository

import com.parvanpajooh.basedomain.models.request.LoginReq
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.*

abstract class BaseRepositoryImpl(
    private val appContract: BaseAppModuleRepository,
    private val dataContract: BaseDataModuleRepository,
    private val deviceContract: BaseDeviceModuleRepository
) : BaseRepository {

    private suspend fun getToken(): Result<String> {
        val userName: String? = PrefHelper.get(BasePrefKey.USERNAME.name)
        return when (userName) {
            null -> Result.Error("unavailable account ", ErrorCode.UNAVAILABLE_ACCOUNT)
            else -> {
                deviceContract.getToken(userName).substitute({ token ->
                    Result.Success(token)
                }, { message, code ->
                    when (code) {
                        ErrorCode.TOKEN_EXPIRED -> {
                            dataContract.getTokenWithRefreshToken(
                                deviceContract.getRefreshToken(userName)
                            ).substitute({ data ->
                                deviceContract.updateAccount(userName, data)
                                Result.Success("${data.tokenType} ${data.accessToken}")
                            }, { message1, code1 ->
                                when (code1) {
                                    ErrorCode.NETWORK_ERROR -> {

                                    }
                                    else -> {
                                        invalidateToken()
                                    }

                                }
                                Result.Error(message1, code1)
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
        }
    }

    protected suspend fun updateTokenWithRefreshToken(): Result<String> {
        return when (val userName: String? = PrefHelper.get(BasePrefKey.USERNAME.name)) {
            null -> Result.Error("unavailable account ", ErrorCode.UNAVAILABLE_ACCOUNT)
            else -> {
                dataContract.getTokenWithRefreshToken(deviceContract.getRefreshToken(userName))
                    .map { data ->
                        deviceContract.updateAccount(userName, data)
                        "${data.tokenType} ${data.accessToken}"
                    }
            }
        }

    }


    protected fun invalidateToken() {
        val userName: String? = PrefHelper.get(BasePrefKey.USERNAME.name)
        if (userName != null) {
            deviceContract.invalidateToken(userName)
        }
    }

    private suspend fun getTokenWithAccount(model: LoginReq): Result<String> {
        return dataContract.getTokenWithAccount(model.username, model.password).whenSucceed {
            PrefHelper.put(BasePrefKey.USERNAME.name, model.username)
            PrefHelper.put(BasePrefKey.LATEST_USERNAME.name, model.username)
            deviceContract.createAccount(model.username, it)
            Result.Success(model.username)
        }
    }

    private suspend fun getTokenWithRefreshToken(username: String): Result<String> {
        return dataContract.getTokenWithRefreshToken(deviceContract.getRefreshToken(username))
            .whenSucceed {
                deviceContract.updateAccount(username, it)
                Result.Success("${it.tokenType} ${it.accessToken}")
            }
    }

    override suspend fun autoLogin(username: String): Result<Unit> {
        return getToken().whenUnAuthorized { getTokenWithRefreshToken(username) }
            .whenSucceed { Result.Success(Unit) }
    }

    override suspend fun initialize(): Result<Unit> {
        return getToken().whenSucceed {
            dataContract.initialize(it)
        }
    }

    override suspend fun login(parameter: LoginReq): Result<Unit> {
        return getTokenWithAccount(parameter).whenSucceed {
            Result.Success(Unit)
        }
    }

    override fun logout() {
        launchIO {
            invalidateToken()
        }
        PrefHelper.delete(BasePrefKey.USERNAME.name)
        appContract.goToLogin()


    }

    protected suspend fun <T : Any> getTokenAndWhenUnauthorizeRetry(func: suspend (token:String) -> Result<T>): Result<T> {
        return getToken().whenSucceed {
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
