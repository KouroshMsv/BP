package com.parvanpajooh.basedomain.repository

import android.app.Activity
import com.parvanpajooh.basedomain.models.response.TokenRes
import dev.kourosh.basedomain.Result

open interface DeviceModuleRepository {

    fun isAccountValid(username: String): Boolean
    fun updateAccount(username: String, data: TokenRes)
    fun getRefreshToken(username: String): String
    fun createAccount(username: String, data: TokenRes)
    suspend fun getToken(username: String): Result<String>
    fun invalidateToken(username: String)

    suspend fun getCurrentLocation(activity: Activity):Result<com.parvanpajooh.basedomain.models.Location>


}