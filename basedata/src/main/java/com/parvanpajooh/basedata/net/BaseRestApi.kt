package com.parvanpajooh.basedata.net

import com.parvanpajooh.basedata.net.models.Token
import dev.kourosh.basedomain.Result
import kotlinx.coroutines.Deferred


open interface BaseRestApi {
    suspend fun getTokenWithAccountAsync(username: String, password: String): Result<Token>
    suspend fun getTokenWithRefreshTokenAsync(refreshToken: String): Result<Token>
}