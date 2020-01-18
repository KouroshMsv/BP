package com.parvanpajooh.baseapp.infrastructure.mvvm.dialog

import com.parvanpajooh.basedomain.interactor.factory.UseCaseFactory
import dev.kourosh.baseapp.infrastructure.mvvm.dialog.BaseDialogViewModel

abstract class BaseDialogViewModel(private val uc: UseCaseFactory) : BaseDialogViewModel() {
}