package com.parvanpajooh.basedata

import com.parvanpajooh.basedata.net.BaseRestApi
import com.parvanpajooh.basedata.net.models.Token
import com.parvanpajooh.basedomain.models.response.TokenRes
import com.parvanpajooh.basedomain.repository.BaseDataModuleRepository
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.logW
import org.json.JSONObject
import retrofit2.HttpException
import java.net.HttpURLConnection

abstract class BaseDataModuleRepositoryImpl(
    private val restApi: BaseRestApi
) : BaseDataModuleRepository {

    override suspend fun getTokenWithAccount(username: String, password: String): Result<TokenRes> {
        return checkResponseError {
            val result = restApi.getTokenWithAccountAsync(username, password).await()
            Result.Success(result.toDomain())
        }
    }

    override suspend fun getTokenWithRefreshToken(refreshToken: String): Result<TokenRes> {
        return checkResponseError {
            val result = restApi.getTokenWithRefreshTokenAsync(refreshToken).await()
            Result.Success(result.toDomain())
        }

    }


    protected suspend fun <T : Any> checkResponseError(service: suspend () -> Result<T>): Result<T> {
        return try {
            service()
        } catch (e: HttpException) {
            logW(e.toString())
            when (e.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> Result.Error(
                    e.code().toString(),
                    ErrorCode.UNAUTHORIZED
                )
                418 -> e.getErrorMessage()
                else -> {
                    Result.Error(e.code().toString(), ErrorCode.SERVER_ERROR)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("خطا در ارتباط با سرور", ErrorCode.NETWORK_ERROR)
        }
    }

    protected fun HttpException.getErrorMessage(): Result.Error {
        return Result.Error(
            JSONObject(
                response()?.errorBody()?.string() ?: "{\"message\":\"پیامی وجود ندارد.\"}"
            ).getString("message"), ErrorCode.SERVER_ERROR
        )
    }


    private fun Token.toDomain() = TokenRes(
        accessToken,
        tokenType.capitalize(),
        refreshToken,
        (expiresIn * 1000) + System.currentTimeMillis()
    )


}
