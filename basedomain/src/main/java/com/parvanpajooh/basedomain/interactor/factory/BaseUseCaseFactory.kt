package com.parvanpajooh.basedomain.interactor.factory

import com.parvanpajooh.basedomain.interactor.usecase.*
import com.parvanpajooh.basedomain.repository.BaseRepository

open class BaseUseCaseFactory(private val repository: BaseRepository) {
    fun initialize() = InitializeUC(repository)
    fun login() = LoginUC(repository)
    fun logout() = LogoutUC(repository)
    fun autoLogin() = AutoLoginUC(repository)
}