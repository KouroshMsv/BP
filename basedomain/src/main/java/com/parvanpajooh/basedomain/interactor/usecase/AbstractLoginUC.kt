package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.models.request.LoginReq
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.interactor.SuspendUseCaseWithParameter

abstract class AbstractLoginUC : AbstractBaseUseCase(), SuspendUseCaseWithParameter<LoginReq, Result<Unit>> {
    override suspend fun execute(parameter: LoginReq): Result<Unit> {
        return dataContract.getTokenWithAccount(parameter.username, parameter.password).map {
            PrefHelper.put(BasePrefKey.USERNAME.name, parameter.username)
            PrefHelper.put(BasePrefKey.LOGGED_IN.name, true)
            Unit
        }
    }
}
