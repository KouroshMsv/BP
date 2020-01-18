package com.parvanpajooh.baseapp.infrastructure.dialog

import androidx.annotation.LayoutRes
import dev.kourosh.baseapp.infrastructure.dialog.BaseDialog

abstract class BaseDialog(
    @LayoutRes private val layoutId: Int
) : BaseDialog() {
    override fun getLayout() = layoutId
}