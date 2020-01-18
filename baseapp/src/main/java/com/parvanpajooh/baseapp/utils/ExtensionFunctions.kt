package com.parvanpajooh.baseapp.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.parvanpajooh.baseapp.enums.NetworkStatus
import com.parvanpajooh.baseapp.infrastructure.dialog.TwoStateMessageDialog
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.accountmanager.accountmanager.AuthenticationCRUD
import dev.kourosh.basedomain.classOf
import dev.kourosh.basedomain.launchIO
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL


fun isOnline3(): Deferred<Boolean> {
    val response = CompletableDeferred<Boolean>()
    launchIO {
        val runtime = Runtime.getRuntime()
        response.complete(
            try {
                val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
                val exitValue = ipProcess.waitFor()
                exitValue == 0
            } catch (e: Exception) {
                false
            }
        )
    }
    return response
}

private val googleUrl = URL("http://clients3.google.com/generate_204")
private val parvanUrl = URL("http://ecourier.mahex.com/generate_204")
private var connection: HttpURLConnection? = null
fun isOnline(parvanUrl: URL = URL("http://ecourier.mahex.com/generate_204")): Deferred<NetworkStatus> {

    val response = CompletableDeferred<NetworkStatus>()
    launchIO {
        response.complete(
            try {
                connection = parvanUrl.openConnection() as HttpURLConnection
                connection!!.connect()
                val connected = connection!!.responseCode == 200
                connection!!.disconnect()
                if (connected)
                    NetworkStatus.Connected
                else
                    checkGoogleServer().await()

            } catch (e: Exception) {
                checkGoogleServer().await()
            } finally {
                if (connection != null) {
                    connection!!.disconnect()
                }
            }
        )
    }
    return response
}

fun checkGoogleServer(): Deferred<NetworkStatus> {
    val response = CompletableDeferred<NetworkStatus>()
    launchIO {
        response.complete(
            try {
                connection = googleUrl.openConnection() as HttpURLConnection
                connection!!.connect()
                val connected = connection!!.responseCode == 204
                connection!!.disconnect()
                if (connected)
                    NetworkStatus.ServerIsDisconnected
                else
                    NetworkStatus.InternetIsDisconnected
            } catch (e: Exception) {
                NetworkStatus.InternetIsDisconnected
            } finally {
                if (connection != null) {
                    connection!!.disconnect()
                }
            }
        )
    }
    return response

}

fun isOnline2(): Deferred<Boolean> {
    val response = CompletableDeferred<Boolean>()
    launchIO {
        response.complete(
            try {
                val timeoutMs = 1500
                val sock = Socket()
                val sockaddr = InetSocketAddress("8.8.8.8", 53)
                sock.connect(sockaddr, timeoutMs)
                sock.close()
                true
            } catch (e: IOException) {
                false
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

fun AppCompatActivity.checkPermission(neededPermissions: List<PermissionRequest>): Boolean {
    var checkNext = true
    neededPermissions.forEach { permissionRequest ->
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
