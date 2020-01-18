package com.parvanpajooh.baseapp.ui.login

import android.content.Intent
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.parvanpajooh.baseapp.infrastructure.mvvm.activity.BaseActivity
import com.parvanpajooh.baseapp.ui.CheckTimeDialog
import com.parvanpajooh.baseapp.ui.main.BaseMainActivity
import com.parvanpajooh.baseapp.utils.PermissionRequest
import dev.kourosh.baseapp.Helpers

abstract class BaseLoginActivity<B : ViewDataBinding,T:Any>(
    @LayoutRes private val layoutId: Int,
    @IdRes private val variable: Int,
    viewModelInstance: LoginActivityVM,
    private val mainActivityClass: Class<T>,
    neededPermissions: List<PermissionRequest>
) : BaseActivity<B, LoginActivityVM>(
    layoutId, variable, viewModelInstance,neededPermissions
) {

    override fun observeVMVariable() {
        vm.successLogin.observe(this, Observer {
            startActivity(Intent(this, mainActivityClass))
            finish()
        })
    }

    override fun onNetworkErrorCancel() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNetworkErrorTryAgain() {
        vm.initialize()
    }

    var dialog: CheckTimeDialog? = null

    override fun onResume() {
        super.onResume()

        if (!Helpers.isTimeAutomatic(this)) {
            if (dialog == null) {
                dialog = CheckTimeDialog.newInstance()
                dialog?.show(supportFragmentManager)
            }
        } else {
            dialog?.dismiss()
            dialog = null
        }
    }
}
