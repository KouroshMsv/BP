package com.parvanpajooh.baseapp.infrastructure

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.fede987.statusbaralert.StatusBarAlert
import com.fede987.statusbaralert.StatusBarAlertView
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.components.service.NetworkStatusService
import com.parvanpajooh.baseapp.enums.NetworkStatus
import com.parvanpajooh.baseapp.models.eventbus.NetworkEvent
import com.parvanpajooh.baseapp.utils.PermissionRequest
import com.parvanpajooh.baseapp.utils.checkPermission
import com.parvanpajooh.baseapp.utils.isOnline
import dev.kourosh.baseapp.onMain
import dev.kourosh.basedomain.classOf
import dev.kourosh.basedomain.launchIO
import dev.kourosh.basedomain.logE
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class BaseActivity(
    @LayoutRes private val layoutId: Int,
    private val neededPermissions: List<PermissionRequest>

) : AppCompatActivity() {


    private lateinit var statusBarAlertView: StatusBarAlert.Builder
    private lateinit var notworkStatusIntent: Intent
    lateinit var statusBarAlert: StatusBarAlertView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        if (!NetworkStatusService.running) {
            notworkStatusIntent = Intent(applicationContext, classOf<NetworkStatusService>())
            startService(notworkStatusIntent)
        }
        statusBarAlertView = StatusBarAlert.Builder(this)
            .autoHide(false)
            .showProgress(true)
            .withAlertColor(R.color.colorPrimaryDark)
        launchIO {
            delay(200)
            val status = isOnline().await()
            onMain {
                when (status) {
                    NetworkStatus.InternetIsDisconnected -> showConnecting(status.message)
                    NetworkStatus.ServerIsDisconnected -> showConnecting(status.message)
                    NetworkStatus.Connected -> showConnected(status.message)
                }
            }
        }
    }
    abstract fun init()


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChange(event: NetworkEvent) {
        if (event.connected) {
            showConnected(event.message)
        } else {
            showConnecting(event.message)

        }
    }

    private fun showConnecting(message: String) {
        try {

            if (!statusIsHidden()) {
                statusBarAlert.hideIndeterminateProgress()
            }

            statusBarAlert = statusBarAlertView.withText(message).build()!!
            statusBarAlert.layoutDirection = View.LAYOUT_DIRECTION_RTL
            statusBarAlert.showIndeterminateProgress()
        } catch (e: Exception) {
            logE(e)
        }
    }

    private fun showConnected(message: String) {
        StatusBarAlert.hide(this, Runnable { })
    }

    private fun statusIsHidden(): Boolean {
        return StatusBarAlert.allAlerts[this.componentName.className].isNullOrEmpty()
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
        if (neededPermissions.isNotEmpty())
            if (checkPermission(neededPermissions)){
                init()
            }
        (applicationContext as BaseApp).currentActivity = this
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (neededPermissions.isNotEmpty() && requestCode in PermissionRequest.values().map { it.requestCode })
            if (checkPermission(neededPermissions)){
                init()
            }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }


}
