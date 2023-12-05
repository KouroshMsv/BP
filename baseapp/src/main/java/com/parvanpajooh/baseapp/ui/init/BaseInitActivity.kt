package com.parvanpajooh.baseapp.ui.init

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.infrastructure.BaseActivity
import com.parvanpajooh.baseapp.models.UpdateModel
import com.parvanpajooh.baseapp.ui.TwoStateMessageDialog
import com.parvanpajooh.baseapp.utils.PermissionRequest
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.baseapp.launchMain
import dev.kourosh.basedomain.globalScope
import dev.kourosh.basedomain.logD
import dev.kourosh.basedomain.logE
import dev.kourosh.metamorphosis.Builder
import dev.kourosh.metamorphosis.Metamorphosis
import dev.kourosh.metamorphosis.OnCheckVersionListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


abstract class BaseInitActivity<MAIN : Any, LOGIN : Any>(
    updateUrl: String,
    private val mainActivityClass: Class<MAIN>,
    private val loginActivityClass: Class<LOGIN>,
    requiredPermissions: List<PermissionRequest>,
    private val backgroundColorId: Int = R.color.colorPrimaryDark,
    @ColorRes private val textColorId: Int = R.color.white
) : BaseActivity(R.layout.activity_init, requiredPermissions) {
    private val versionCode by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(applicationContext.packageName, 0).longVersionCode
        } else {
            packageManager.getPackageInfo(applicationContext.packageName, 0).versionCode.toLong()
        }
    }
    private val loggedIn: Boolean by lazy {
        PrefHelper.get(
            BasePrefKey.INITIALIZED.name,
            false
        ) && PrefHelper.get<Boolean?>(BasePrefKey.LOGGED_IN.name) == true
    }
    private val metamorphosis by lazy { Metamorphosis(Builder(this, updateUrl)) }
    private val json = Json

    override fun onCreate(savedInstanceState: Bundle?) {
        PrefHelper.put(
            BasePrefKey.VERSION_NAME.name,
            packageManager.getPackageInfo(applicationContext.packageName, 0).versionName
        )
        super.onCreate(savedInstanceState)
        findViewById<AppCompatTextView>(R.id.initActivityTxtUpdating).setTextColor(
            ContextCompat.getColor(
                this,
                textColorId
            )
        )
        findViewById<AppCompatTextView>(R.id.initActivityTxtAppName).setTextColor(
            ContextCompat.getColor(
                this,
                textColorId
            )
        )
        findViewById<ConstraintLayout>(R.id.initActivityRoot).setBackgroundColor(
            ContextCompat.getColor(
                this,
                backgroundColorId
            )
        )

    }

    private fun nextActivity() {
        globalScope.launchMain {
            if (loggedIn) {
                startActivity(Intent(this@BaseInitActivity, mainActivityClass))
            } else {
                startActivity(Intent(this@BaseInitActivity, loginActivityClass))
            }
            finish()
        }
    }

    private val onCheckVersionListener = object : OnCheckVersionListener {
        override fun onFailed(message: String, code: Int?) {
            logD("message: $message ,code: $code")
            nextActivity()
        }

        override fun onSucceed(data: String) {
            val updaterRes = parseJson(data)
            if (updaterRes != null) {
                if (updaterRes.latestVersionCode > versionCode) {
                    updateNewVersion(updaterRes)
                } else {
                    nextActivity()
                }
            } else {
                nextActivity()
            }
        }
    }

    private fun parseJson(data: String): UpdateModel? {
        return try {
            json.decodeFromString<UpdateModel>(data)
        } catch (e: Exception) {
            onParseJsonError(data, e)
            null
        }

    }

    private fun checkVersion() {
        metamorphosis.checkVersion(onCheckVersionListener)
    }


    private fun updateNewVersion(updaterRes: UpdateModel) {
        if (updaterRes.required) {
            startDownload(updaterRes.url)
        } else {
            val dialog = TwoStateMessageDialog.newInstance(
                message = "نسخه جدید اپلیکیشن آماده دانلود است.\nبرای دریافت نسخه جدید روی دکمه دریافت کلیک کنید.",
                positiveButtonText = "دریافت",
                negativeButtonText = "فعلا نه",
                cancellable = false
            )
            dialog.negativeButtonClickListener {
                dialog.dismiss()
                nextActivity()
            }
            dialog.positiveButtonClickListener {
                dialog.dismiss()
                startDownload(updaterRes.url)
            }
            dialog.show(supportFragmentManager)
        }
    }

    private fun startDownload(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(browserIntent)
        } catch (e: Exception) {
            Toast.makeText(
                this, "No application can handle this request."
                        + " Please install a webbrowser", Toast.LENGTH_LONG
            ).show();
            e.printStackTrace();
        }
    }

    override fun permissionChecked() {
        checkVersion()
    }

    open fun onParseJsonError(json: String, e: Exception) {
        logE("json: $json\nexception: $e")
    }

}