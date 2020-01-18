package com.parvanpajooh.baseapp.infrastructure.mvvm.fragment

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import dev.kourosh.baseapp.infrastructure.mvvm.fragment.BaseShareFragment

abstract class BaseShareFragment<B : ViewDataBinding, VM : BaseFragmentViewModel>(
    @LayoutRes private val layoutId: Int,
    @IdRes private val variable: Int,
    viewModelInstance: VM) : BaseShareFragment<B, VM>(layoutId, variable, viewModelInstance)