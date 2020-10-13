package com.parvanpajooh.basedomain.interactor.factory

import com.parvanpajooh.basedomain.interactor.usecase.InitializeUCI
import com.parvanpajooh.basedomain.interactor.usecase.LoginUCI

interface UseCaseListI {
    val initialize: InitializeUCI
    val login: LoginUCI
}