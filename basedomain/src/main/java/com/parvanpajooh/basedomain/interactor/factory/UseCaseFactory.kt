package com.parvanpajooh.basedomain.interactor.factory

import com.parvanpajooh.basedomain.interactor.usecase.*
import com.parvanpajooh.basedomain.repository.Repository

open class UseCaseFactory(private val repository: Repository) {
    fun initialize() = InitializeUC(repository)
    fun login() = LoginUC(repository)
    fun autoLogin() = AutoLoginUC(repository)
}