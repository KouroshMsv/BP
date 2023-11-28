package com.parvanpajooh.baseapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.button.MaterialButton
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.infrastructure.BaseDialog

class CheckTimeDialog : BaseDialog(R.layout.dialog_check_time) {

    companion object {
        fun newInstance(): CheckTimeDialog {
            return CheckTimeDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, dev.kourosh.baseapp.R.style.FullScreenDialogStyle)
    }

    override fun initView(v: View) {

        isCancelable = false
        v.findViewById<MaterialButton>(R.id.btnTimeSetting).setOnClickListener {
            startActivity(Intent(Settings.ACTION_DATE_SETTINGS))
        }
    }
}