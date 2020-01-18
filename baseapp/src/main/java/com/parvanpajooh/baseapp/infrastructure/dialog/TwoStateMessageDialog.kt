package com.parvanpajooh.baseapp.infrastructure.dialog

import android.view.View
import androidx.core.os.bundleOf
import com.parvanpajooh.baseapp.R
import kotlinx.android.synthetic.main.dialog_two_state_message.*


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
        private var instance: TwoStateMessageDialog? = null

        fun newInstance(
            message: String,
            positiveButtonText: String = "بله",
            negativeButtonText: String = "خیر",
            cancellable: Boolean = true
        ): TwoStateMessageDialog {
            if (instance == null) {
                instance = TwoStateMessageDialog()
            } else {
                instance!!.dismiss()
            }

            instance!!.arguments = bundleOf(
                "cancellable" to cancellable,
                "message" to message,
                "negativeButtonText" to negativeButtonText,
                "positiveButtonText" to positiveButtonText
            )
            return instance!!
        }
    }


    override fun initView(v: View) {
        arguments!!.run {
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