package com.parvanpajooh.baseapp.infrastructure.mvvm.activity

import com.parvanpajooh.basedomain.interactor.factory.UseCaseFactory
import dev.kourosh.baseapp.infrastructure.mvvm.activity.BaseActivityViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseActivityViewModel : BaseActivityViewModel(), KoinComponent {
    val uc: UseCaseFactory by inject()

}