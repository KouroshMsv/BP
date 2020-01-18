package com.parvanpajooh.basedata.net

import com.parvanpajooh.basedata.net.models.Token
import kotlinx.coroutines.Deferred


open interface RestApi {
    suspend fun getTokenWithAccountAsync(username: String, password: String): Deferred<Token>
    suspend fun getTokenWithRefreshTokenAsync(refreshToken: String): Deferred<Token>
}