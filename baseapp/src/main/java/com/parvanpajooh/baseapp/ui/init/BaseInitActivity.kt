package com.parvanpajooh.baseapp.ui.init

import android.app.DownloadManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.infrastructure.BaseActivity
import com.parvanpajooh.baseapp.models.UpdateModel
import com.parvanpajooh.baseapp.ui.TwoStateMessageDialog
import com.parvanpajooh.baseapp.utils.PermissionRequest
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.logD
import dev.kourosh.basedomain.logE
import dev.kourosh.metamorphosis.Builder
import dev.kourosh.metamorphosis.Metamorphosis
import dev.kourosh.metamorphosis.OnCheckVersionListener
import dev.kourosh.metamorphosis.OnDownloadListener
import kotlinx.android.synthetic.main.activity_init.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File


abstract class BaseInitActivity<MAIN : Any, LOGIN : Any>(
    updateUrl: String,
    private val apkName: String,
    private val mainActivityClass: Class<MAIN>,
    private val loginActivityClass: Class<LOGIN>,
    requiredPermissions: List<PermissionRequest>,
    private val backgroundColorId: Int = R.color.colorPrimaryDark,
    @ColorRes private val textColorId: Int = R.color.white
) : BaseActivity(R.layout.activity_init, requiredPermissions) {
    private val versionCode by lazy {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
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
        PrefHelper.put(BasePrefKey.VERSION_NAME.name, packageManager.getPackageInfo(applicationContext.packageName, 0).versionName)
        super.onCreate(savedInstanceState)
        initActivityTxtUpdating.setTextColor(ContextCompat.getColor(this, textColorId))
        initActivityTxtAppName.setTextColor(ContextCompat.getColor(this, textColorId))
        initActivityRoot.setBackgroundColor(ContextCompat.getColor(this, backgroundColorId))


        metamorphosis.downloadListener = object : OnDownloadListener {
            override fun onFailed(message: String, code: Int?) {
                changeProgressVisibility(false)
                logD("message: $message ,code: $code")
                nextActivity()
            }

            override fun onFinished(file: File) {
                changeProgressVisibility(false)
                metamorphosis.installAPK(file)
            }
        }
        metamorphosis.setOnDownloadingListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                progress.setProgress(it, true)
            } else {
                progress.progress = it
            }
        }
    }

    private fun changeProgressVisibility(visible: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            lyrProgress.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    private fun nextActivity() {
        GlobalScope.launch(Dispatchers.Main) {

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
                metamorphosis.builder.apkName = "${apkName}_${updaterRes.latestVersion}.apk"
                metamorphosis.builder.notificationConfig.title =
                    "${apkName}_${updaterRes.latestVersion}.apk"
                metamorphosis.builder.notificationConfig.notificationVisibility =
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                val lastApk = File("${metamorphosis.builder.dir}/${metamorphosis.builder.apkName}")
                if (lastApk.exists() && updaterRes.latestVersionCode > versionCode) {
                    updateNewVersion(updaterRes, lastApk)
                } else {
                    if (updaterRes.latestVersionCode > versionCode)
                        updateNewVersion(updaterRes, lastApk)
                    else
                        nextActivity()
                }
            } else {
                nextActivity()
            }
        }
    }

    private fun parseJson(data: String): UpdateModel? {
        return try {
            json.decodeFromString(UpdateModel.serializer(), data)
        } catch (e: Exception) {
            onParseJsonError(data, e)
            null
        }

    }

    private fun checkVersion() {
        metamorphosis.checkVersion(onCheckVersionListener)
    }


    private fun updateNewVersion(updaterRes: UpdateModel, apk: File) {
        if (updaterRes.required) {
            if (apk.exists()) {
                metamorphosis.installAPK(apk)
            } else {
                startDownload(updaterRes.url)
            }
        } else {
            val dialog = TwoStateMessageDialog.newInstance(
                if (apk.exists()) "نسخه جدید اپلیکیشن دانلود شده است.\nبرای نصب نسخه جدید روی دکمه نصب کلیک کنید." else "نسخه جدید اپلیکیشن آماده دانلود است.\nبرای دریافت نسخه جدید روی دکمه دریافت کلیک کنید.",
                if (apk.exists()) "نصب" else "دریافت", "فعلا نه", false
            )
            dialog.negativeButtonClickListener {
                nextActivity()
            }
            dialog.positiveButtonClickListener {
                dialog.dismiss()
                if (apk.exists()) {
                    metamorphosis.installAPK(apk)
                } else {
                    startDownload(updaterRes.url)
                }
            }
            dialog.show(supportFragmentManager)
        }
    }

    private fun startDownload(url: String) {
        changeProgressVisibility(true)
        metamorphosis.startDownload(url)
    }

    override fun permissionChecked() {
        checkVersion()
    }

    open fun onParseJsonError(json: String, e: Exception) {
        logE("json: $json\nexception: $e")
    }

}