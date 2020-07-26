package com.parvanpajooh.baseapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import com.parvanpajooh.baseapp.infrastructure.BaseActivity
import com.parvanpajooh.baseapp.ui.CheckTimeDialog
import com.parvanpajooh.baseapp.utils.PermissionRequest
import com.parvanpajooh.basedomain.interactor.factory.BaseUseCaseFactory
import com.parvanpajooh.basedomain.models.request.LoginReq
import com.parvanpajooh.basedomain.utils.sharedpreferences.BasePrefKey
import com.parvanpajooh.basedomain.utils.sharedpreferences.PrefHelper
import dev.kourosh.baseapp.Helpers
import dev.kourosh.baseapp.dialogs.NetworkErrorDialog
import dev.kourosh.baseapp.enums.MessageType
import dev.kourosh.baseapp.hideKeyboard
import dev.kourosh.baseapp.numP2E
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseLoginActivity<T : Any>(
        @LayoutRes private val layoutId: Int,
        @IdRes private val progressViewId: Int,
        @IdRes private val loginButtonId: Int,
        @IdRes private val usernameId: Int,
        @IdRes private val passwordId: Int,
        private val mainActivityClass: Class<T>,
        requiredPermissions: List<PermissionRequest>
) : BaseActivity(
        layoutId, requiredPermissions
) {
    private lateinit var progressView: View
    private lateinit var loginButton: AppCompatButton
    private lateinit var username: AppCompatEditText
    private lateinit var password: AppCompatEditText
    private var loading: Boolean = false
        set(value) {
            field = value
            if (field) {
                progressView.visibility = View.VISIBLE
                loginButton.isEnabled = false
                username.isEnabled = false
                password.isEnabled = false
            } else {
                progressView.visibility = View.GONE
                loginButton.isEnabled = true
                username.isEnabled = true
                password.isEnabled = true

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressView = findViewById(progressViewId)
        loginButton = findViewById(loginButtonId)
        username = findViewById(usernameId)
        password = findViewById(passwordId)
        username.setText(com.parvanpajooh.basedomain.utils.username)
        loginButton.setOnClickListener {
            login()
        }
    }

    fun startMainActivity() {
        startActivity(Intent(this, mainActivityClass))
        finish()
    }

    var dialog: CheckTimeDialog? = null

    override fun onResume() {
        super.onResume()

        if (!Helpers.isTimeAutomatic(this)) {
            if (dialog == null) {
                dialog = CheckTimeDialog.newInstance()
                dialog?.show(supportFragmentManager)
            }
        } else {
            dialog?.dismiss()
            dialog = null
        }
    }

    abstract val uc: BaseUseCaseFactory
    fun login() {
        hideKeyboard(this)
        when {
            username.text.isNullOrEmpty() -> showSnackBar(
                    "نام کاربری وارد نشده است",
                    MessageType.WARNING
            )
            password.text.isNullOrEmpty() -> showSnackBar(
                    "رمز عبور وارد نشده است",
                    MessageType.WARNING
            )
            else -> {
                loading = true
                launchIO {
                    uc.login().execute(
                            LoginReq(
                                    username.text!!.toString().numP2E(),
                                    password.text!!.toString().numP2E()
                            )
                    )
                            .parseOnMain({
                                initialize()
                            }, { message, errorCode ->
                                loading = false
                                showSnackBar(message, MessageType.ERROR)
                            })
                }
            }

        }


    }

    private fun showNetworkError() {
        val dialog = NetworkErrorDialog()
        dialog.showCancel = false
        dialog.onRetryClickListener = View.OnClickListener {
            initialize()
            dialog.dismiss()
        }
        dialog.show(supportFragmentManager)
    }

    private fun initialize() {
        lifecycleScope.launch(Dispatchers.Main) {
            if (!PrefHelper.get(BasePrefKey.INITIALIZED.name, false)) {
                loading = true
                launchIO {
                    uc.initialize().execute().parseOnMain({
                        loading = false
                        startMainActivity()
                    }, { message, errorCode ->
                        loading = false
                        showNetworkError()
                    })

                }
            } else {
                startMainActivity()
            }
        }
    }


    override fun permissionChecked() {

    }

    private fun forgetPassword() {
        hideKeyboard(this)
        showSnackBar("این قابلیت در نسخه های بعدی اضافه می شود :)", MessageType.INFO)
    }


    private fun showSnackBar(message: String, messageType: MessageType) {
        dev.kourosh.baseapp.showSnackBar(
                loginButton,
                applicationContext,
                message,
                messageType
        )
    }
}
