package com.parvanpajooh.baseapp.ui.login

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.parvanpajooh.baseapp.infrastructure.mvvm.activity.BaseActivityViewModel
import com.parvanpajooh.basedomain.models.request.LoginReq
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.baseapp.SingleLiveEvent
import dev.kourosh.baseapp.parseOnMain
import dev.kourosh.baseapp.start
import dev.kourosh.basedomain.launchIO

abstract class LoginActivityVM : BaseActivityViewModel() {
    var userName = ObservableField<String>()
    var password = ObservableField<String>()
    var isLoading = ObservableBoolean()
    var successLogin = SingleLiveEvent<Nothing>()
    fun login() {
        hideKeyboard()
        when {
            userName.get().isNullOrEmpty() -> showWarning("نام کاربری وارد نشده است")
            password.get().isNullOrEmpty() -> showWarning("رمز عبور وارد نشده است")
            else -> {
                isLoading.set(true)
                launchIO {
                    uc.login().execute(LoginReq(userName.get()!!, password.get()!!))
                        .parseOnMain(isLoading, {
                            initialize()
                        }, { message, errorCode ->
                            showError(message)
                        })
                }
            }

        }


    }

    fun initialize() {
        if (!PrefHelper.get(BasePrefKey.INITIALIZED.name, false)) {
            isLoading.start()
            launchIO {
                uc.initialize().execute().parseOnMain(isLoading, {
                    successLogin.call()
                }, { message, errorCode ->
                    showWarning(message)

                    showNetworkError(false)
                })

            }
        } else {
            successLogin.call()

        }
    }


    fun forgetPassword() {
        hideKeyboard()
        showInfo("این قابلیت در نسخه های بعدی اضافه می شود :)")
    }

}