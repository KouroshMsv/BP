package com.parvanpajooh.baseparvan

import android.os.Bundle
import com.parvanpajooh.baseapp.ui.login.BaseLoginActivity
import com.parvanpajooh.basedomain.interactor.factory.BaseUseCaseI
import com.parvanpajooh.basedomain.interactor.usecase.InitializeUCI
import com.parvanpajooh.basedomain.interactor.usecase.LoginUCI
import dev.kourosh.basedomain.logE
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat

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

    override val uc: BaseUseCaseI = TODO("Not yet implemented")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentDateTime = PersianDateFormat("l d/m/Y H:i").format(PersianDate(System.currentTimeMillis()))
        logE("اطلاعات وارد شده نامعتبر است.\n$currentDateTime")
    }
}