package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.repository.BaseAppModuleRepository
import com.parvanpajooh.basedomain.repository.BaseDataModuleRepository
import com.parvanpajooh.basedomain.repository.BaseDeviceModuleRepository
import com.parvanpajooh.basedomain.utils.username
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.whenUnAuthorized

abstract class AbstractBaseUseCase {

    abstract val appContract: BaseAppModuleRepository
    abstract val dataContract: BaseDataModuleRepository
    abstract val deviceContract: BaseDeviceModuleRepository

    abstract fun logout()

    protected suspend fun getToken(): Result<String> {
        return deviceContract.getToken(username)
            .substitute({ token -> Result.Success(token) }, { message, code ->
                when (code) {
                    ErrorCode.TOKEN_EXPIRED -> {
                        dataContract.getTokenWithRefreshToken(
                            deviceContract.getRefreshToken(
                                username
                            )
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

    protected suspend fun <T : Any> getTokenAndRetryWhenUnauthorize(func: suspend (token: String) -> Result<T>): Result<T> {
        return getToken().bind { func(it) }.whenUnAuthorized {

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