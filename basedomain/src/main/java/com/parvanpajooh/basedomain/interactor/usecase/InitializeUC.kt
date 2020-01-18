package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.repository.Repository
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.interactor.SuspendUseCase

class InitializeUC(private val repository: Repository) :
    SuspendUseCase<Result<Unit>> {
    override suspend fun execute() = repository.initialize()
}