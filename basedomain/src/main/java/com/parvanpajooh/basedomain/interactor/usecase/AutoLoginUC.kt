package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.repository.Repository
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.interactor.SuspendUseCaseWithParameter

class AutoLoginUC(private val repository: Repository) :
    SuspendUseCaseWithParameter<String, Result<Unit>> {
    override suspend fun execute(parameter: String) = repository.autoLogin(parameter)
}
