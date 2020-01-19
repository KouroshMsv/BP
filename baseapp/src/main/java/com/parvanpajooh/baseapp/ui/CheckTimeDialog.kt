package com.parvanpajooh.baseapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.FragmentManager
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.infrastructure.BaseDialog
import kotlinx.android.synthetic.main.dialog_check_time.*

class CheckTimeDialog : BaseDialog(R.layout.dialog_check_time) {

    companion object {
        var instance: CheckTimeDialog? = null
        var visible: Boolean = false

        fun newInstance(): CheckTimeDialog {
            if (instance == null)
                instance = CheckTimeDialog()
            return instance!!
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun initView(v: View) {

        isCancelable = false
        btnTimeSetting.setOnClickListener {
            startActivity(Intent(Settings.ACTION_DATE_SETTINGS))

        }
    }

    override fun dismiss() {
        super.dismiss()
        visible = false
    }

    override fun show(manager: FragmentManager) {
        super.show(manager)
        visible = true
    }
}