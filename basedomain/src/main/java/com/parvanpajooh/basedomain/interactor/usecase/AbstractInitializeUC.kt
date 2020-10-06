package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.interactor.SuspendUseCase

abstract class AbstractInitializeUC : AbstractBaseUseCase(), SuspendUseCase<Result<Unit>> {
    override suspend fun execute() = if (!PrefHelper.get(BasePrefKey.INITIALIZED.name, false)) {
        getToken().bind { dataContract.initialize(it) }
    } else {
        Result.Success(Unit)
    }
}