package com.parvanpajooh.baseapp.components.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.parvanpajooh.baseapp.ui.login.BaseLoginActivity
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.accountmanager.accountmanager.Authenticator
import dev.kourosh.basedomain.classOf

abstract class BaseAuthenticatorService<T:Any> : Service() {
    abstract val loginActivityClass: Class<T>
    private var authenticator: Authenticator? = null
    override fun onBind(intent: Intent?): IBinder {
        if (authenticator == null) {
            authenticator =
                Authenticator(
                    this,
                    loginActivityClass,
                    getString(PrefHelper.get(BasePrefKey.AUTHORITY.name))
                )
        }
        return authenticator!!.iBinder
    }
}