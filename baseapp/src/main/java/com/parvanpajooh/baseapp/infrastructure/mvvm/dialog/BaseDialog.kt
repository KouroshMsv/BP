package com.parvanpajooh.baseapp.infrastructure.mvvm.dialog

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import dev.kourosh.baseapp.infrastructure.mvvm.dialog.BaseDialog


abstract class BaseDialog<B : ViewDataBinding, VM : BaseDialogViewModel>(
    @LayoutRes private val layoutId: Int,
    @IdRes private val variable: Int,
    viewModelInstance: VM
)  : BaseDialog<B, VM>(layoutId, variable, viewModelInstance)
