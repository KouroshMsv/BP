package com.parvanpajooh.baseapp.infrastructure.mvvm.fragment

import com.parvanpajooh.basedomain.interactor.factory.UseCaseFactory
import dev.kourosh.baseapp.infrastructure.mvvm.fragment.BaseFragmentViewModel

abstract class BaseFragmentViewModel(private val uc: UseCaseFactory) : BaseFragmentViewModel() {

}
