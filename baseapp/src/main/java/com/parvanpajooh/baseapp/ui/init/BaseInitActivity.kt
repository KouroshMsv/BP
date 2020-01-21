package com.parvanpajooh.baseapp.ui.init

import android.app.DownloadManager
import android.content.Intent
import android.view.View
import com.google.gson.Gson
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.infrastructure.BaseActivity
import com.parvanpajooh.baseapp.models.UpdateModel
import com.parvanpajooh.baseapp.ui.TwoStateMessageDialog
import com.parvanpajooh.baseapp.utils.PermissionRequest
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.logD
import dev.kourosh.metamorphosis.Builder
import dev.kourosh.metamorphosis.Metamorphosis
import dev.kourosh.metamorphosis.OnCheckVersionListener
import dev.kourosh.metamorphosis.OnDownloadListener
import kotlinx.android.synthetic.main.activity_init.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


abstract class BaseInitActivity<MAIN : Any, LOGIN : Any>(
    updateUrl: String,
    private val apkName: String,
    private val versionCode: Int,
    private val mainActivityClass: Class<MAIN>,
    private val loginActivityClass: Class<LOGIN>,
    requiredPermissions: List<PermissionRequest>
) : BaseActivity(R.layout.activity_init, requiredPermissions), OnCheckVersionListener {
    private val loggedIn: Boolean by lazy {
        PrefHelper.get(
            BasePrefKey.INITIALIZED.name,
            false
        ) && PrefHelper.get<String?>(BasePrefKey.USERNAME.name) != null
    }
    private val gson = Gson()
    private val metamorphosis = Metamorphosis(Builder(this, updateUrl))

    init {

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

    private fun checkVersion() {
        metamorphosis.checkVersion(this)
    }

    override fun onFailed(message: String, code: Int?) {
        logD("message: $message ,code: $code")
        nextActivity()
    }


    override fun onSucceed(data: String) {
        val updaterRes = gson.fromJson(data, UpdateModel::class.java)
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
}