package com.parvanpajooh.baseapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.parvanpajooh.baseapp.infrastructure.App
import com.parvanpajooh.basedomain.repository.AppModuleRepository

class AppModuleRepositoryImpl(private val context: Context,
                              private val baseLoginActivityClass: Class<AppCompatActivity>
) : AppModuleRepository {
    override fun goToLogin() {
        context.startActivity(Intent(context, baseLoginActivityClass))
        (context as App).currentActivity?.finish()
    }
}
