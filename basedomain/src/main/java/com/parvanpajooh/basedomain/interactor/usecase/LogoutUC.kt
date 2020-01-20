package com.parvanpajooh.basedomain.interactor.usecase

import com.parvanpajooh.basedomain.repository.BaseRepository
import dev.kourosh.basedomain.interactor.UseCase

class LogoutUC(private val repository: BaseRepository) : UseCase<Unit> {
    override fun execute() = repository.logout()
}
