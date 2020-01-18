package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.repository.BaseRepository
import dev.kourosh.basedomain.Result
import dev.kourosh.basedomain.interactor.SuspendUseCase

class InitializeUC(private val repository: BaseRepository) :
    SuspendUseCase<Result<Unit>> {
    override suspend fun execute() = repository.initialize()
}