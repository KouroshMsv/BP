package com.parvanpajooh.basedomain.repository

import com.parvanpajooh.basedomain.models.request.*
import dev.kourosh.basedomain.Result


open interface BaseRepository {
    suspend fun initialize(): Result<Unit>
    suspend fun login(parameter: LoginReq): Result<Unit>
    suspend fun autoLogin(username: String): Result<Unit>
    suspend fun logout()

}