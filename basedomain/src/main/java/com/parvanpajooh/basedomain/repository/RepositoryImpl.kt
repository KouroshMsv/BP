package com.parvanpajooh.basedomain.repository

import com.parvanpajooh.basedomain.models.request.LoginReq
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.*

open class RepositoryImpl(
    private val appContract: AppModuleRepository,
    private val dataContract: DataModuleRepository,
    private val deviceContract: DeviceModuleRepository
) :  Repository {

    protected suspend fun getToken(): Result<String> {
        val userName: String = PrefHelper.get(BasePrefKey.USERNAME.name)
        return deviceContract.getToken(userName).substitute({ token ->
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

    protected suspend fun updateTokenWithRefreshToken(): Result<String> {
        val userName: String = PrefHelper.get(BasePrefKey.USERNAME.name)
        return dataContract.getTokenWithRefreshToken(deviceContract.getRefreshToken(userName))
            .map { data ->
                deviceContract.updateAccount(userName, data)
                "${data.tokenType} ${data.accessToken}"
            }
    }


    protected fun invalidateToken() {
        val userName: String = PrefHelper.get(BasePrefKey.USERNAME.name)
        deviceContract.invalidateToken(userName)
    }
    private suspend fun getTokenWithAccount(model: LoginReq): Result<String> {
        return dataContract.getTokenWithAccount(model.username, model.password).whenSucceed {
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
        }.checkAuthenticationAndRetry {
            initialize()
        }
    }

    override suspend fun login(parameter: LoginReq): Result<Unit> {
        return getTokenWithAccount(parameter).whenSucceed {
            PrefHelper.put(BasePrefKey.USERNAME.name, it)
            Result.Success(Unit)
        }
    }

    override suspend fun logout() {
        invalidateToken()


    }

    protected suspend fun <T : Any> Result<T>.checkAuthenticationAndRetry(retry: suspend () -> Result<T>): Result<T> {
        return whenUnAuthorized {
            updateTokenWithRefreshToken().substitute({ _ ->
                retry()
                it
            }, { _, _ ->
                PrefHelper.delete(BasePrefKey.USERNAME.name)
                appContract.goToLogin()
                it
            })
        }
    }

}