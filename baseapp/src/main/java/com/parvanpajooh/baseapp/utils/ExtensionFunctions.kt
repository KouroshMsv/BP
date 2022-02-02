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
import dev.kourosh.basedomain.globalScope
import dev.kourosh.basedomain.launchIO
import dev.kourosh.basedomain.logE
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
            parvanUrl.readText()
            NetworkStatus.Connected
        } catch (e: Exception) {
            checkGoogleServer()
        }
    )
    return response.await()
}

suspend fun checkGoogleServer(googleUrl: URL = internetUrl): NetworkStatus {
    val response = CompletableDeferred<NetworkStatus>()
    globalScope.launchIO {
        response.complete(
            try {
                googleUrl.readText()
                NetworkStatus.ServerIsDisconnected
            } catch (e: Exception) {
                NetworkStatus.InternetIsDisconnected
            }
        )
    }
    return response.await()

}

fun startSync(accountHelper: AuthenticationCRUD, bundle: Bundle = Bundle()) {
    val username = PrefHelper.get<String?>(BasePrefKey.USERNAME.name, null)
    if (username != null) {
        val account = accountHelper.getAccount(username)
        if (account != null) {
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
            ContentResolver.requestSync(
                accountHelper.getAccount(username),
                PrefHelper.get(BasePrefKey.AUTHORITY.name),
                bundle
            )
        } else
            logE("addPeriodicSync -> Account is null")
    } else {
        logE("addPeriodicSync -> username is null")
    }
}


fun addPeriodicSync(accountHelper: AuthenticationCRUD, interval: Long, bundle: Bundle = Bundle()) {
    val username = PrefHelper.get<String?>(BasePrefKey.USERNAME.name, null)
    if (username != null) {
        val account = accountHelper.getAccount(username)
        if (account != null) {
            ContentResolver.setIsSyncable(account, PrefHelper.get(BasePrefKey.AUTHORITY.name), 1)
            ContentResolver.setSyncAutomatically(account, PrefHelper.get(BasePrefKey.AUTHORITY.name), true)
            ContentResolver.addPeriodicSync(account, PrefHelper.get(BasePrefKey.AUTHORITY.name), bundle, interval)
        } else
            logE("addPeriodicSync -> Account is null")
    } else {
        logE("addPeriodicSync -> username is null")
    }
}

internal const val batchPermissionCode = 999
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
        permission.isEmpty()
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
    startActivity(Intent(this, T::class.java))
    if (finished)
        finish()
}
