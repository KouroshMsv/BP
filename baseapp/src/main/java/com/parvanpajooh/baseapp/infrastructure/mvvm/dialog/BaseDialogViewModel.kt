package com.parvanpajooh.baseapp.infrastructure.mvvm.dialog

import com.parvanpajooh.basedomain.interactor.factory.UseCaseFactory
import dev.kourosh.baseapp.infrastructure.mvvm.dialog.BaseDialogViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

abstract class BaseDialogViewModel : BaseDialogViewModel(), KoinComponent {
    val uc: UseCaseFactory by inject()
}