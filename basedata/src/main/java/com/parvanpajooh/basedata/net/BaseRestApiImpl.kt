package com.parvanpajooh.basedata.net

import com.parvanpajooh.basedata.net.apis.TokenApi
import com.parvanpajooh.basedata.net.models.Token
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.logW
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.HttpException
import java.net.HttpURLConnection


open class BaseRestApiImpl(tokenUrl: String, debuggable: Boolean) : BaseRestApi {
    val AUTHORIZATION = "Authorization"
    private val tokenApi = BaseApiService(tokenUrl, debuggable, true, 20).create<TokenApi>()

    override suspend fun getTokenWithAccountAsync(
        username: String,
        password: String
    ): Result<Token> {
        return checkResponseError {
            tokenApi.getTokenWithAccountAsync(username, password)
        }
    }

    override suspend fun getTokenWithRefreshTokenAsync(refreshToken: String): Result<Token> {
        return checkResponseError {
            tokenApi.getTokenWithRefreshTokenAsync(refreshToken)
        }
    }


    protected suspend fun <T : Any> checkResponseError(service: suspend () -> Deferred<T>): Result<T> {
        return try {
            Result.Success(service().await())
        } catch (e: HttpException) {
            logW(e.toString())
            when (e.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> Result.Error(
                    "اطلاعات وارد شده نامعتبر است.",
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
}