package com.parvanpajooh.baseapp.components.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.accountmanager.accountmanager.Authenticator

abstract class BaseAuthenticatorService<T : Any> : Service() {
    abstract val loginActivityClass: Class<T>
    private var authenticator: Authenticator? = null
    override fun onBind(intent: Intent?): IBinder {
        if (authenticator == null) {
            authenticator =
                Authenticator(
                    this,
                    loginActivityClass,
                    PrefHelper.get(BasePrefKey.AUTHORITY.name)
                )
        }
        return authenticator!!.iBinder
    }
}