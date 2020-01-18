package com.parvanpajooh.basedomain.repository

import com.parvanpajooh.basedomain.models.response.*
import dev.kourosh.basedomain.Result

open interface BaseDataModuleRepository {
    suspend fun initialize(token: String): Result<Unit>
    suspend fun getTokenWithAccount(username: String, password: String): Result<TokenRes>
    suspend fun getTokenWithRefreshToken(refreshToken: String): Result<TokenRes>

}
