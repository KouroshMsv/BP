package com.parvanpajooh.basedomain.interactor.usecase

import dev.kourosh.basedomain.Result
import com.parvanpajooh.basedomain.models.request.LoginReq
import com.parvanpajooh.basedomain.repository.Repository
import dev.kourosh.basedomain.interactor.SuspendUseCaseWithParameter

class LoginUC(private val repository: Repository) : SuspendUseCaseWithParameter<LoginReq, Result<Unit>> {
  override suspend fun execute(parameter: LoginReq) = repository.login(parameter)
}
