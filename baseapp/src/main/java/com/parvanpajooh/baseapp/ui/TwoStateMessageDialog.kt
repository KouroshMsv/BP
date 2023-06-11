package com.parvanpajooh.baseapp.ui

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.os.bundleOf
import com.google.android.material.button.MaterialButton
import com.parvanpajooh.baseapp.R
import com.parvanpajooh.baseapp.infrastructure.BaseDialog


class TwoStateMessageDialog private constructor() : BaseDialog(R.layout.dialog_two_state_message) {

    private var onNegativeButtonClickListener: View.OnClickListener? = null
    private var onPositiveButtonClickListener: View.OnClickListener? = null

    fun negativeButtonClickListener(func: ((v: View) -> Unit)) {

        onNegativeButtonClickListener = View.OnClickListener {
            func(it)
        }
    }

    fun positiveButtonClickListener(func: ((v: View) -> Unit)) {

        onPositiveButtonClickListener = View.OnClickListener {
            func(it)
        }
    }


    companion object {

        fun newInstance(
            message: String,
            positiveButtonText: String = "بله",
            negativeButtonText: String = "خیر",
            cancellable: Boolean = true
        ): TwoStateMessageDialog {
            val instance= TwoStateMessageDialog()
            instance.arguments = bundleOf(
                "cancellable" to cancellable,
                "message" to message,
                "negativeButtonText" to negativeButtonText,
                "positiveButtonText" to positiveButtonText
            )
            return instance
        }
    }


    override fun initView(v: View) {
        val txtMessage = v.findViewById<AppCompatTextView>(R.id.txtMessage)
        val btnPositive = v.findViewById<MaterialButton>(R.id.btnPositive)
        val btnNegative = v.findViewById<MaterialButton>(R.id.btnNegative)
        arguments?.run {
            isCancelable = getBoolean("cancellable")
            txtMessage.text = getString("message")
            btnPositive.text = getString("positiveButtonText")
            btnNegative.text = getString("negativeButtonText")
        }
        btnNegative.setOnClickListener {
            onNegativeButtonClickListener?.onClick(v)
            dismiss()
        }
        btnPositive.setOnClickListener {
            onPositiveButtonClickListener?.onClick(v)
            dismiss()
        }
    }
}