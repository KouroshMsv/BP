package com.parvanpajooh.baseapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.parvanpajooh.baseapp.infrastructure.BaseApp
import com.parvanpajooh.basedomain.repository.BaseAppModuleRepository

abstract class BaseAppModuleRepositoryImpl<T : AppCompatActivity>(
    private val context: Context,
    private val baseLoginActivityClass: Class<T>
) : BaseAppModuleRepository {
    override fun goToLogin() {
        val baseApp = context as BaseApp
        if (baseApp.currentActivity::class.java != baseLoginActivityClass) {
            val intent=Intent(baseApp, baseLoginActivityClass)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            baseApp.startActivity(intent)
            baseApp.currentActivity.finish()
        }
    }
}
