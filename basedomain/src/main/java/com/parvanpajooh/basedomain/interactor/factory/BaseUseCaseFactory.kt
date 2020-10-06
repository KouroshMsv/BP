package com.parvanpajooh.basedomain.interactor.factory

import com.parvanpajooh.basedomain.interactor.usecase.AbstractInitializeUC
import com.parvanpajooh.basedomain.interactor.usecase.AbstractLoginUC

abstract class BaseUseCaseFactory {
    abstract val initialize: AbstractInitializeUC
    abstract val login: AbstractLoginUC
}