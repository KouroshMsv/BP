package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.repository.BaseRepository
import dev.kourosh.basedomain.interactor.SuspendUseCase

class LogoutUC(private val repository: BaseRepository) : SuspendUseCase<Unit> {
    override suspend fun execute() = repository.logout()
}
