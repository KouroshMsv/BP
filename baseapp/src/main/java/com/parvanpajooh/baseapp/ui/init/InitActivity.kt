package com.parvanpajooh.baseapp.ui.init

import android.app.DownloadManager
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.parvanpajooh.baseapp.BR
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.databinding.ActivityInitBinding
import com.parvanpajooh.baseapp.infrastructure.dialog.TwoStateMessageDialog
import com.parvanpajooh.baseapp.infrastructure.mvvm.activity.BaseActivity
import com.parvanpajooh.baseapp.models.UpdateModel
import dev.kourosh.basedomain.logD
import dev.kourosh.metamorphosis.Builder
import dev.kourosh.metamorphosis.Metamorphosis
import dev.kourosh.metamorphosis.OnCheckVersionListener
import dev.kourosh.metamorphosis.OnDownloadListener
import kotlinx.android.synthetic.main.activity_init.*
import java.io.File


abstract class InitActivity<MAIN : Any, LOGIN : Any>(
    updateUrl: String,
    private val apkName: String,
    private val versionCode: Int,
    private val mainActivityClass: Class<MAIN>,
    private val loginActivityClass: Class<LOGIN>
) : BaseActivity<ActivityInitBinding, InitActivityVM>(
    R.layout.activity_init,
    BR.initA,
    InitActivityVM(), listOf()
), OnCheckVersionListener {
    var loggedIn = false
    private val gson = Gson()
    private val metamorphosis = Metamorphosis(Builder(this, updateUrl))
    override fun observeVMVariable() {
        metamorphosis.downloadListener = object : OnDownloadListener {
            override fun onFailed(message: String, code: Int?) {
                lyrProgress.visibility = View.GONE
                logD("message: $message ,code: $code")
                vm.autoLogin()
            }

            override fun onFinished(file: File) {
                lyrProgress.visibility = View.GONE
                metamorphosis.installAPK(file)
            }
        }


        vm.loggedIn.observe(this, Observer { loggedIn ->
            this.loggedIn = loggedIn
            nextActivity()
        })
    }

    private fun nextActivity() {
        if (loggedIn) {
            startActivity(Intent(this, mainActivityClass))
        } else {
            startActivity(Intent(this, loginActivityClass))
        }
        finish()
    }

    override fun onNetworkErrorCancel() {}

    override fun onNetworkErrorTryAgain() {}

    private fun checkVersion() {
        metamorphosis.checkVersion(this)
    }


    override fun onFailed(message: String, code: Int?) {
        logD("message: $message ,code: $code")
        vm.autoLogin()
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
                vm.autoLogin()
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
                vm.autoLogin()
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
        lyrProgress.visibility = View.VISIBLE
        metamorphosis.startDownload(url)
    }

    override fun onResume() {
        super.onResume()
        checkVersion()
    }
}