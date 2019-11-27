package com.feedbacktower.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.ActivityLoginScreenBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.utils.ConnectivityReceiver
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.ui.register.RegisterPhoneScreen
import com.feedbacktower.util.Constants
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.navigateUser
import kotlinx.android.synthetic.main.activity_login_screen.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class LoginScreen : BaseViewActivityImpl(), LoginContract.View, ConnectivityReceiver.ConnectivityReceiverListener {
    private val TAG = "LoginScreen"
    @Inject
    lateinit var presenter: LoginPresenter
    private lateinit var binding: ActivityLoginScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).appComponent.authComponent().create()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_screen)
        presenter.attachView(this)
        initUi()
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun initUi() {
        with(binding) {
            onSignUpClicked = View.OnClickListener {
                launchActivity<RegisterPhoneScreen> {
                    putExtra(RegisterPhoneScreen.SCREEN_TYPE_KEY, RegisterPhoneScreen.ScreenFunction.REGISTER)
                }
            }
            onForgotClicked = View.OnClickListener {
                launchActivity<RegisterPhoneScreen> {
                    putExtra(RegisterPhoneScreen.SCREEN_TYPE_KEY, RegisterPhoneScreen.ScreenFunction.FORGOT_PASSWORD)
                }
            }
            onLoginClicked = View.OnClickListener {
                val phone = mobileInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                if (!validCredentials(phone, password)) {
                    return@OnClickListener
                }
                if (!ConnectivityReceiver.isConnected()) {
                    toast("Not connected to the internet")
                } else {
                    presenter.login(phone, password)
                }
            }
        }
    }

    private fun validCredentials(phone: String, password: String): Boolean {
        textInputPhone.error = null
        textInputPassword.error = null
        return when {
            phone.isEmpty() || phone.length != Constants.PHONE_LENGTH -> {
                textInputPhone.error = "Enter valid phone number"
                false
            }
            password.isEmpty() -> {
                textInputPassword.error = "Enter valid password"
                false
            }
            password.length < Constants.MIN_PASSWORD_LENGTH -> {
                textInputPassword.error = "Password must be ${Constants.MIN_PASSWORD_LENGTH} characters long"
                false
            }
            password.length > Constants.MAX_PASSWORD_LENGTH -> {
                textInputPassword.error = "Password must be less than ${Constants.MAX_PASSWORD_LENGTH} characters long"
                false
            }
            else -> true
        }
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        toast(error.message)
    }

    override fun onLoginSuccess(user: User) {
        navigateUser(user)
    }

    override fun showProgress() {
        super.showProgress()
        loginButton.isEnabled = false
        loginButton.text = getString(R.string.please_wait_dot_dot)
    }

    override fun dismissProgress() {
        super.dismissProgress()
        loginButton.isEnabled = true
        loginButton.text = getString(R.string.log_in)
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Log.d(TAG, "IS_CONNECTED: $isConnected")
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
