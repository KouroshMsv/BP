package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.models.request.LoginReq
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.interactor.SuspendUseCaseWithParameter

interface LoginUCI : SuspendUseCaseWithParameter<LoginReq, Result<Unit>>
