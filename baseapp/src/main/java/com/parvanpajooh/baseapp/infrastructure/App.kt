package com.parvanpajooh.baseapp.infrastructure

import android.app.Activity
import android.app.Application
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.baseapp.Helpers
import dev.kourosh.baseapp.initApp


open class App : Application() {
    var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        initApp()
        PrefHelper.init(applicationContext)
        PrefHelper.put(BasePrefKey.IS_AUTO_TIME_ZONE.name, Helpers.isTimeAutomatic(this))
    }

}