package com.parvanpajooh.baseapp.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.parvanpajooh.baseapp.enums.NetworkStatus
import com.parvanpajooh.baseapp.ui.TwoStateMessageDialog
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.accountmanager.accountmanager.AuthenticationCRUD
import dev.kourosh.basedomain.classOf
import dev.kourosh.basedomain.launchIO
import kotlinx.coroutines.CompletableDeferred
import java.net.HttpURLConnection
import java.net.URL

private var connection: HttpURLConnection? = null

private val serverUrl by lazy {
    URL(PrefHelper.get(BasePrefKey.SERVER_CHECK_URL.name, "http://ecourier.mahex.com/generate_204"))
}
private val internetUrl by lazy {
    URL(
        PrefHelper.get(
            BasePrefKey.INTERNET_CHECK_URL.name,
            "http://clients3.google.com/generate_204"
        )
    )
}

suspend fun isOnline(parvanUrl: URL = serverUrl): NetworkStatus {

    val response = CompletableDeferred<NetworkStatus>()
    response.complete(
        try {
            connection = parvanUrl.openConnection() as HttpURLConnection
            connection?.connectTimeout = 2000
            connection?.connect()
            val connected = connection?.responseCode == 200
            connection?.disconnect()
            if (connected)
                NetworkStatus.Connected
            else
                checkGoogleServer()

        } catch (e: Exception) {
            connection?.disconnect()
            checkGoogleServer()
        }
    )
    return response.await()
}

suspend fun checkGoogleServer(googleUrl: URL = internetUrl): NetworkStatus {
    val response = CompletableDeferred<NetworkStatus>()
    launchIO {
        response.complete(
            try {
                connection = googleUrl.openConnection() as HttpURLConnection
                connection?.connectTimeout = 2000
                connection?.connect()
                val connected = connection?.responseCode == 204
                connection?.disconnect()
                if (connected)
                    NetworkStatus.ServerIsDisconnected
                else
                    NetworkStatus.InternetIsDisconnected

            } catch (e: Exception) {
                connection?.disconnect()
                NetworkStatus.InternetIsDisconnected
            }
        )
    }
    return response.await()

}

fun startSync(accountHelper: AuthenticationCRUD) {
    val settingsBundle = Bundle()
    settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
    settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
    ContentResolver.requestSync(
        accountHelper.getAccount(PrefHelper.get(BasePrefKey.USERNAME.name)),
        PrefHelper.get(BasePrefKey.AUTHORITY.name),
        settingsBundle
    )
}

internal val batchPermissionCode = 999
fun AppCompatActivity.checkPermission(
    requiredPermissions: List<PermissionRequest>,
    batchCheck: Boolean
): Boolean {
    return if (batchCheck) {
        val permission = requiredPermissions.filter {
            checkPermission(it) != PermissionResponse.GRANTED
        }
        if (permission.isNotEmpty()) {
            val dialog = TwoStateMessageDialog.newInstance(
                "برای استفاده از تمامی امکانات اپلیکیشن اجازه دسترسی به مجوز های مورد نیاز را بدهید.",
                "اجازه میدهم",
                "بستن برنامه", false
            )
            dialog.negativeButtonClickListener {
                finish()
            }
            dialog.positiveButtonClickListener {
                requestPermission(permission, batchPermissionCode)
            }
            dialog.show(supportFragmentManager)
        }
        permission.isNotEmpty()
    } else {
        var checkNext = true
        requiredPermissions.forEach { permissionRequest ->
            when (checkPermission(permissionRequest)) {
                PermissionResponse.GRANTED -> {
                    checkNext = true
                }
                PermissionResponse.FIRST_DENIED, PermissionResponse.DENIED -> {
                    if (checkNext) {
                        checkNext = false
//                    requestPermission(permissionRequest)
                        val dialog = TwoStateMessageDialog.newInstance(
                            permissionRequest.message,
                            "اجازه میدهم",
                            "بستن برنامه", false
                        )
                        dialog.negativeButtonClickListener {
                            finish()
                        }
                        dialog.positiveButtonClickListener {
                            requestPermission(permissionRequest)

                        }
                        dialog.show(supportFragmentManager)
                        return checkNext
                    }
                }
            }
        }
        checkNext
    }
}

inline fun <reified T> Activity.startActivity(finished: Boolean = true) {
    startActivity(Intent(this, classOf<T>()))
    if (finished)
        finish()
}
