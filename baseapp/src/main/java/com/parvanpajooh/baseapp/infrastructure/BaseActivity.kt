package com.parvanpajooh.baseapp.infrastructure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fede987.statusbaralert.StatusBarAlert
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.components.service.NetworkStatusService
import com.parvanpajooh.baseapp.enums.NetworkStatus
import com.parvanpajooh.baseapp.models.eventbus.NetworkEvent
import com.parvanpajooh.baseapp.utils.PermissionRequest
import com.parvanpajooh.baseapp.utils.batchPermissionCode
import com.parvanpajooh.baseapp.utils.checkPermission
import com.parvanpajooh.baseapp.utils.isOnline
import dev.kourosh.baseapp.onMain
import dev.kourosh.basedomain.logE
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class BaseActivity(
    @LayoutRes private val layoutId: Int,
    private val requiredPermissions: List<PermissionRequest>

) : AppCompatActivity() {


    private lateinit var statusBarAlertView: StatusBarAlert.Builder
    private lateinit var notworkStatusIntent: Intent
    var statusBarAlert: StatusBarAlert?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        if (!NetworkStatusService.running) {
            notworkStatusIntent = Intent(applicationContext, NetworkStatusService::class.java)
            startService(notworkStatusIntent)
        }
        statusBarAlertView = StatusBarAlert.Builder(this)
            .autoHide(false)
            .showProgress(true)
            .alertColor(R.color.colorPrimaryDark)
        lifecycleScope.launch(Dispatchers.IO) {
            delay(200)
            val status = isOnline()
            onMain {
                when (status) {
                    NetworkStatus.InternetIsDisconnected -> showConnecting(status.message)
                    NetworkStatus.ServerIsDisconnected -> showConnecting(status.message)
                    NetworkStatus.Connected -> showConnected()
                }
            }
        }
    }


    abstract fun permissionChecked()


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChange(event: NetworkEvent) {
        if (event.connected) {
            showConnected()
        } else {
            showConnecting(event.message)

        }
    }

    private fun showConnecting(message: String) {
        try {

            if (!statusIsHidden()) {
                statusBarAlert?.hideProgress()
            }

            statusBarAlert = statusBarAlertView.text(message).build()
            statusBarAlert?.layoutDirection = View.LAYOUT_DIRECTION_RTL
            statusBarAlert?.show()
        } catch (e: Exception) {
            logE(e)
        }
    }

    private fun showConnected() {
        statusBarAlert?.hide {  }
    }

    private fun statusIsHidden(): Boolean {
        return !(statusBarAlert?.isShown?:false)
    }


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }


    override fun onResume() {
        super.onResume()
        (applicationContext as BaseApp).currentActivity = this
        if (requiredPermissions.isNotEmpty())
            if (checkPermission(requiredPermissions, true)) {
                permissionChecked()
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == batchPermissionCode) {
            if (checkPermission(requiredPermissions, true)) {
                permissionChecked()
            }
        } else if (requiredPermissions.isNotEmpty() && requestCode in PermissionRequest.values()
                .map { it.requestCode }
        ) {
            if (checkPermission(requiredPermissions, false)) {
                permissionChecked()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    fun launchIO(block: suspend CoroutineScope.() -> Unit) {
        lifecycleScope.launch(Dispatchers.IO) {
            block(this)
        }
    }

}
