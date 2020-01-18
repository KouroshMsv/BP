package com.parvanpajooh.basedomain.interactor.factory

import com.parvanpajooh.basedomain.interactor.usecase.*
import com.parvanpajooh.basedomain.repository.BaseRepository

open class UseCaseFactory(private val repository: BaseRepository) {
    fun initialize() = InitializeUC(repository)
    fun login() = LoginUC(repository)
    fun autoLogin() = AutoLoginUC(repository)
}