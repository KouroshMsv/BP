package com.parvanpajooh.baseapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.parvanpajooh.baseapp.infrastructure.BaseActivity
import com.parvanpajooh.baseapp.ui.CheckTimeDialog
import com.parvanpajooh.baseapp.utils.PermissionRequest
import com.parvanpajooh.basedomain.interactor.factory.BaseUseCaseI
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
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

abstract class BaseLoginActivity<T : Any>(
    @LayoutRes private val layoutId: Int,
    @IdRes private val progressViewId: Int,
    @IdRes private val loginButtonId: Int,
    @IdRes private val loginBiometricButtonId: Int,
    @IdRes private val usernameId: Int,
    @IdRes private val passwordId: Int,
    private val mainActivityClass: Class<T>,
    requiredPermissions: List<PermissionRequest>
) : BaseActivity(layoutId, requiredPermissions) {
    private lateinit var progressView: View
    private lateinit var loginButton: AppCompatButton
    private lateinit var biometricButton: AppCompatButton
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

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("ورود")
            .setDeviceCredentialAllowed(true)
            .build()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressView = findViewById(progressViewId)
        loginButton = findViewById(loginButtonId)
        biometricButton = findViewById(loginBiometricButtonId)
        username = findViewById(usernameId)
        password = findViewById(passwordId)
        username.setText(PrefHelper.get(BasePrefKey.USERNAME.name, ""))
        loginButton.setOnClickListener {
            login()
        }
        biometricButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
//        initBiometric()
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

    abstract val uc: BaseUseCaseI
    private fun login() {
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
                    uc.login.execute(
                        LoginReq(
                            username.text!!.toString().numP2E(),
                            password.text!!.toString().numP2E()
                        )
                    )
                        .parseOnMain({
                            initialize()
                        }, { message, _ ->
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
        dialog.setOnRetryClickListener {
            initialize()
            dialog.dismiss()
        }
        dialog.show(supportFragmentManager)
    }

    private fun initialize() {
        lifecycleScope.launch(Dispatchers.Main) {
            loading = true
            withContext(Dispatchers.IO) {
                uc.initialize.execute().parseOnMain({
                    loading = false
                    startMainActivity()
                }, { _, _ ->
                    loading = false
                    showNetworkError()
                })

            }
        }
    }


    override fun permissionChecked() {

    }

    private fun forgetPassword() {
        hideKeyboard(this)
        showSnackBar("این قابلیت در نسخه های بعدی اضافه می شود :)", MessageType.INFO)
    }

    private fun initBiometric() {
        val biometricManager = BiometricManager.from(this)
        biometricButton.isEnabled =
            biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS

        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        applicationContext,
                        "Authentication error: $errString",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                }
            })


    }

    private fun showSnackBar(message: String, messageType: MessageType) {
        dev.kourosh.baseapp.showSnackBar(loginButton, applicationContext, message, messageType)
    }
}
