package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.repository.BaseRepository
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.interactor.SuspendUseCaseWithParameter

class AutoLoginUC(private val repository: BaseRepository) :
    SuspendUseCaseWithParameter<String, Result<Unit>> {
    override suspend fun execute(parameter: String) = repository.autoLogin(parameter)
}
