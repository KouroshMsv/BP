package com.parvanpajooh.basedata.net

import com.parvanpajooh.basedata.net.apis.TokenApi
import com.parvanpajooh.basedata.net.models.Token
import kotlinx.coroutines.Deferred


open class RestApiImpl(tokenUrl: String) : RestApi {
    protected val AUTHORIZATION = "Authorization"
    private val tokenApi = BaseApiService(tokenUrl, true, 20).create<TokenApi>()

    override suspend fun getTokenWithAccountAsync(
        username: String,
        password: String
    ): Deferred<Token> {
        return tokenApi.getTokenWithAccountAsync(username, password)
    }

    override suspend fun getTokenWithRefreshTokenAsync(refreshToken: String): Deferred<Token> {
        return tokenApi.getTokenWithRefreshTokenAsync(refreshToken)
    }

}