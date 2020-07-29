package com.parvanpajooh.basedata

import com.parvanpajooh.basedata.net.BaseRestApi
import com.parvanpajooh.basedomain.repository.BaseDataModuleRepository
import dev.kourosh.basedomain.Result

abstract class BaseDataModuleRepositoryImpl(private val restApi: BaseRestApi) : BaseDataModuleRepository {

    override suspend fun getTokenWithAccount(username: String, password: String): Result<TokenRes> {
        return restApi.getTokenWithAccountAsync(username, password).map { it.toDomain() }
    }

    override suspend fun getTokenWithRefreshToken(refreshToken: String): Result<TokenRes> {
        return restApi.getTokenWithRefreshTokenAsync(refreshToken).map { it.toDomain() }
    }

    private fun com.parvanpajooh.basedata.net.models.TokenRes.toDomain() = TokenRes(accessToken, tokenType.capitalize(), refreshToken, (expiresIn * 1000) + System.currentTimeMillis())

}
