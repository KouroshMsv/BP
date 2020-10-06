package com.parvanpajooh.baseapp.infrastructure

import androidx.annotation.LayoutRes
import dev.kourosh.baseapp.infrastructure.dialog.BaseDialog

abstract class BaseDialog(@LayoutRes private val layoutId: Int) : BaseDialog(layoutId)