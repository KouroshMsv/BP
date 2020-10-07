package com.parvanpajooh.basedomain.interactor.usecase

import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.interactor.SuspendUseCase

interface InitializeUCI : SuspendUseCase<Result<Unit>> 