package com.parvanpajooh.baseparvan

import com.parvanpajooh.baseapp.BaseAppModuleRepositoryImpl
import com.parvanpajooh.baseapp.ui.login.BaseLoginActivity
import com.parvanpajooh.basedevice.BaseDeviceModuleRepositoryImpl
import com.parvanpajooh.basedevice.LocationManager
import com.parvanpajooh.basedomain.interactor.factory.BaseUseCaseFactory
import com.parvanpajooh.basedomain.repository.BaseRepositoryImpl

class LoginActivity : BaseLoginActivity<MainActivity>(
    R.layout.activity_login,
    R.id.prg,
    R.id.btnLogin,
    R.id.btnBiometric,
    R.id.edtUsername,
    R.id.edtPassword,
    MainActivity::class.java,
    listOf()
) {

    override val uc: BaseUseCaseFactory
        get() = BaseUseCaseFactory(object : BaseRepositoryImpl(object :
            BaseAppModuleRepositoryImpl<LoginActivity>(applicationContext, javaClass) {},
            Api(), object : BaseDeviceModuleRepositoryImpl(
                dev.kourosh.accountmanager.accountmanager.AuthenticationCRUD(
                    applicationContext,
                    ""
                ),
                LocationManager(applicationContext)
            ) {}) {})
}