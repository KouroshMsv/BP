package com.parvanpajooh.basedata.net

import com.parvanpajooh.basedata.net.models.TokenRes
import dev.kourosh.basedomain.Result


open interface BaseRestApi {
    suspend fun getTokenWithAccountAsync(username: String, password: String): Result<TokenRes>
    suspend fun getTokenWithRefreshTokenAsync(refreshToken: String): Result<TokenRes>
}