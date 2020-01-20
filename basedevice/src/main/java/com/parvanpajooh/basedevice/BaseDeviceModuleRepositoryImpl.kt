package com.parvanpajooh.basedevice

import android.app.Activity
import com.parvanpajooh.basedomain.models.Location
import com.parvanpajooh.basedomain.models.response.TokenRes
import com.parvanpajooh.basedomain.repository.BaseDeviceModuleRepository
import dev.kourosh.accountmanager.UserDataKeys
import dev.kourosh.accountmanager.accountmanager.AuthenticationCRUD
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.map
import kotlinx.coroutines.delay

abstract class BaseDeviceModuleRepositoryImpl(
    private val authenticationCRUD: AuthenticationCRUD,
    private val locationManager: LocationManager
) : BaseDeviceModuleRepository {
    override fun isAccountValid(username: String) = authenticationCRUD.isAvailableAccount(username)
    override fun updateAccount(username: String, data: TokenRes) {
        authenticationCRUD.updateUserData(
            username,
            hashMapOf(
                UserDataKeys.ACCESS_TOKEN to "${data.tokenType} ${data.accessToken}",
                UserDataKeys.REFRESH_TOKEN to data.refreshToken,
                UserDataKeys.EXPIRE_IN to "${data.expiresIn}",
                UserDataKeys.TOKEN_TYPE to data.tokenType
            )
        )
    }

    override fun getRefreshToken(username: String) =
        authenticationCRUD.getUserData(username, UserDataKeys.REFRESH_TOKEN)!!

    override fun createAccount(username: String, data: TokenRes) {
        authenticationCRUD.createOrUpdateAccount(
            username,
            null,
            "${data.tokenType} ${data.accessToken}",
            hashMapOf(
                UserDataKeys.ACCESS_TOKEN to "${data.tokenType} ${data.accessToken}",
                UserDataKeys.REFRESH_TOKEN to data.refreshToken,
                UserDataKeys.EXPIRE_IN to "${data.expiresIn}",
                UserDataKeys.TOKEN_TYPE to data.tokenType
            )
        )
    }

    override suspend fun getToken(username: String): Result<String> {
        return authenticationCRUD.getToken(username)
    }

    override fun invalidateToken(username: String) {
        authenticationCRUD.invalidToken(username)
    }

    override suspend fun getCurrentLocation(activity: Activity): Result<Location> {
        return locationManager.requestGPSSettings(activity).await()
            .map { Location(it.latitude, it.longitude) }


    }
}
