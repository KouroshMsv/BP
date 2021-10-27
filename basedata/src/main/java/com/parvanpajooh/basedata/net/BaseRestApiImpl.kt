package com.parvanpajooh.basedata.net

import com.parvanpajooh.basedata.net.apis.TokenApi
import com.parvanpajooh.basedata.net.models.TokenRes
import dev.kourosh.basedomain.ErrorCode
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.logW
import okio.IOException
import org.json.JSONObject
import retrofit2.HttpException
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat


open class BaseRestApiImpl(tokenUrl: String, debuggable: Boolean) : BaseRestApi {
    val AUTHORIZATION = "Authorization"
    private val tokenApi = BaseApiService(tokenUrl, debuggable, true, 20).create<TokenApi>()

    override suspend fun getTokenWithAccountAsync(
        username: String,
        password: String
    ): Result<TokenRes> {
        return checkResponseError {
            tokenApi.getTokenWithAccountAsync(username, password)
        }
    }


    override suspend fun getTokenWithRefreshTokenAsync(refreshToken: String): Result<TokenRes> {
        return checkResponseError {
            tokenApi.getTokenWithRefreshTokenAsync(refreshToken)
        }
    }


    protected suspend fun <T : Any> checkResponseError(service: suspend () -> T): Result<T> {
        val currentDateTime = PersianDateFormat("l Y/m/d H:i").format(PersianDate(System.currentTimeMillis()))

        return try {
            Result.Success(service())
        } catch (e: HttpException) {

            val httpError = HttpErrorCode.values().single { it.code == e.code() }
            logW(e.toString())
            when (httpError) {

                HttpErrorCode.HTTP_UNAUTHORIZED -> Result.Error(
                    "اطلاعات وارد شده نامعتبر است.\n$currentDateTime",
                    ErrorCode.UNAUTHORIZED
                )
                HttpErrorCode.EC_ERROR -> e.getErrorMessage()
                else -> {
                    Result.Error(
                        "HTTP-${httpError.code}:[ ${httpError.message} ]\n$currentDateTime",
                        ErrorCode.SERVER_ERROR
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            when (e) {
                is IOException -> {
                    Result.Error("خطا در برقراری ارتباط با اینترنت\n$currentDateTime", ErrorCode.NETWORK_ERROR)
                }
                else -> {
                    Result.Error(
                        "${e.message ?: e.localizedMessage ?: e.cause.toString()}\n$currentDateTime",
                        ErrorCode.UNKNOWN
                    )
                }
            }
        }
    }

    enum class HttpErrorCode(val code: Int, val message: String) {

        HTTP_BAD_REQUEST(400, "خطای داخلی"),
        HTTP_UNAUTHORIZED(401, "خطای داخلی"),
        HTTP_PAYMENT_REQUIRED(402, "خطای داخلی"),
        HTTP_FORBIDDEN(403, "خطای داخلی"),
        HTTP_NOT_FOUND(404, "خطای داخلی"),
        HTTP_BAD_METHOD(405, "خطای داخلی"),
        HTTP_NOT_ACCEPTABLE(406, "خطای داخلی"),
        HTTP_PROXY_AUTH(407, "خطای داخلی"),
        HTTP_CLIENT_TIMEOUT(408, "خطای داخلی"),
        HTTP_CONFLICT(409, "خطای داخلی"),
        HTTP_GONE(410, "خطای داخلی"),
        HTTP_LENGTH_REQUIRED(411, "خطای داخلی"),
        HTTP_PRECON_FAILED(412, "خطای داخلی"),
        HTTP_ENTITY_TOO_LARGE(413, "خطای داخلی"),
        HTTP_REQ_TOO_LONG(414, "خطای داخلی"),
        HTTP_UNSUPPORTED_TYPE(415, "خطای داخلی"),
        EC_ERROR(418, ""),
        HTTP_INTERNAL_ERROR(500, "خطای سرور"),
        HTTP_NOT_IMPLEMENTED(501, ""),
        HTTP_BAD_GATEWAY(502, "خطا در برقراری ارتباط با سرور"),
        HTTP_UNAVAILABLE(503, "خطا در دسترسی به سرور"),
        HTTP_GATEWAY_TIMEOUT(504, "خطا در برقراری ارتباط با سرور"),
        HTTP_VERSION(505, ""),
    }

    protected fun HttpException.getErrorMessage(): Result.Error {
        return Result.Error(
            JSONObject(
                response()?.errorBody()?.string() ?: "{\"message\":\"پیامی وجود ندارد.\"}"
            ).getString("message"), ErrorCode.SERVER_ERROR
        )
    }
}