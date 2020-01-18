package com.parvanpajooh.baseapp.infrastructure.mvvm.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.fede987.statusbaralert.StatusBarAlert
import com.fede987.statusbaralert.StatusBarAlertView
import dev.kourosh.basedomain.classOf
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.components.service.NetworkStatusService
import com.parvanpajooh.baseapp.enums.NetworkStatus
import com.parvanpajooh.baseapp.infrastructure.App
import com.parvanpajooh.baseapp.utils.checkPermission
import com.parvanpajooh.baseapp.utils.isOnline
import com.parvanpajooh.ecourier.models.eventbus.NetworkEvent
import com.parvanpajooh.ecourier.utils.PermissionRequest
import dev.kourosh.baseapp.infrastructure.mvvm.activity.BaseActivity
import dev.kourosh.baseapp.onMain
import dev.kourosh.basedomain.launchIO
import dev.kourosh.basedomain.logE
import kotlinx.coroutines.delay
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.Exception


abstract class BaseActivity<B : ViewDataBinding, VM : BaseActivityViewModel>(
    @LayoutRes private val layoutId: Int,
    @IdRes private val variable: Int,
    viewModelInstance: VM,
    private val checkPermission: Boolean = true
) : BaseActivity<B, VM>(layoutId, variable, viewModelInstance) {
    private lateinit var statusBarAlertView: StatusBarAlert.Builder
    private lateinit var notworkStatusIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChange(event: NetworkEvent) {
        if (event.connected) {
            showConnected(event.message)
        } else {
            showConnecting(event.message)

        }
    }

    lateinit var statusBarAlert: StatusBarAlertView
    private fun showConnecting(message: String) {
        try {

            if (!statusIsHidden()) {
                statusBarAlert.hideIndeterminateProgress()
            }

            statusBarAlert = statusBarAlertView.withText(message).build()!!
            statusBarAlert.layoutDirection = View.LAYOUT_DIRECTION_RTL
            statusBarAlert.showIndeterminateProgress()
        }catch (e:Exception){
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
        if (checkPermission)
            checkPermission(true)
        (applicationContext as App).currentActivity=this
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkPermission && requestCode in PermissionRequest.values().map { it.requestCode })
            checkPermission(true)
    }


}