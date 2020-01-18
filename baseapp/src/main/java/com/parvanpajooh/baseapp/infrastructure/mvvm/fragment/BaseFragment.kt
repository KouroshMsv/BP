package com.parvanpajooh.baseapp.infrastructure.mvvm.fragment

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.parvanpajooh.baseapp.models.eventbus.TitleEvent
import dev.kourosh.baseapp.infrastructure.mvvm.fragment.BaseFragment
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment<B : ViewDataBinding, VM : BaseFragmentViewModel>(
    @LayoutRes private val layoutId: Int,
    @IdRes private val variable: Int,
    viewModelInstance: VM
) : BaseFragment<B, VM>(layoutId, variable, viewModelInstance) {


    override fun onNetworkErrorCancel() {
    }

    override fun onNetworkErrorTryAgain() {
    }

    fun setTitle(title: String) {
        EventBus.getDefault().post(TitleEvent(title))
    }
}