package com.parvanpajooh.baseapp.infrastructure.mvvm.fragment

import com.parvanpajooh.basedomain.interactor.factory.UseCaseFactory
import dev.kourosh.baseapp.infrastructure.mvvm.fragment.BaseFragmentViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseFragmentViewModel : BaseFragmentViewModel(), KoinComponent {
    val uc: UseCaseFactory by inject()

}
