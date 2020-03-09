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
import kotlinx.coroutines.Deferred
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

fun isOnline(parvanUrl: URL = serverUrl): Deferred<NetworkStatus> {

    val response = CompletableDeferred<NetworkStatus>()
    launchIO {
        response.complete(
            try {
                connection = parvanUrl.openConnection() as HttpURLConnection
                connection?.connectTimeout = 2000
                connection?.connect()
                val connected = connection?.responseCode == 200
                if (connected)
                    NetworkStatus.Connected
                else
                    checkGoogleServer().await()

            } catch (e: Exception) {
                checkGoogleServer().await()
            } finally {
                connection?.disconnect()
            }
        )
    }
    return response
}

fun checkGoogleServer(googleUrl: URL = internetUrl): Deferred<NetworkStatus> {
    val response = CompletableDeferred<NetworkStatus>()
    launchIO {
        response.complete(
            try {
                connection = googleUrl.openConnection() as HttpURLConnection
                connection?.connectTimeout = 2000
                connection?.connect()
                val connected = connection?.responseCode == 204
                if (connected)
                    NetworkStatus.ServerIsDisconnected
                else
                    NetworkStatus.InternetIsDisconnected
            } catch (e: Exception) {
                NetworkStatus.InternetIsDisconnected
            } finally {
                connection?.disconnect()
            }
        )
    }
    return response

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

fun AppCompatActivity.checkPermission(requiredPermissions: List<PermissionRequest>): Boolean {
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
    return checkNext
}

inline fun <reified T> Activity.startActivity(finished: Boolean = true) {
    startActivity(Intent(this, classOf<T>()))
    if (finished)
        finish()
}
