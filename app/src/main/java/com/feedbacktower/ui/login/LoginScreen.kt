package com.feedbacktower.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.ActivityLoginScreenBinding
import com.feedbacktower.network.manager.AuthManager
import com.feedbacktower.network.utils.ConnectivityReceiver
import com.feedbacktower.ui.register.RegisterPhoneScreen
import com.feedbacktower.util.Constants
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.navigateUser
import kotlinx.android.synthetic.main.activity_login_screen.*
import org.jetbrains.anko.toast

class LoginScreen : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    companion object {
        private const val TAG = "LoginScreen"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginScreenBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login_screen)
        initUi(binding)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun initUi(binding: ActivityLoginScreenBinding) {
        with(binding) {
            onSignUpClicked = View.OnClickListener { launchActivity<RegisterPhoneScreen>{
                putExtra(RegisterPhoneScreen.SCREEN_TYPE_KEY, RegisterPhoneScreen.ScreenFunction.REGISTER)
            } }
            onForgotClicked = View.OnClickListener { launchActivity<RegisterPhoneScreen>{
                putExtra(RegisterPhoneScreen.SCREEN_TYPE_KEY, RegisterPhoneScreen.ScreenFunction.FORGOT_PASSWORD)
            } }
            onLoginClicked = View.OnClickListener {
                val phone = mobileInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                if (!validCredentials(phone, password)) {
                    return@OnClickListener
                }
                if (!ConnectivityReceiver.isConnected()) {
                    toast("Not connected to the internet")
                } else {
                    tryLogin(phone, password)
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

    private fun tryLogin(phone: String, password: String) {
        showLoading()
        AuthManager.getInstance().phoneLogin(phone, password)
        { response, error ->
            hideLoading()
            if (error != null) {
                toast(error.message)
                return@phoneLogin
            }

            if (response != null) {
                AppPrefs.getInstance(this).apply {
                    user = response.user
                    authToken = response.token
                }
                navigateUser(response.user)
            } else {
                toast("Could not get response")
            }

        }
    }

    private fun showLoading() {
        loginButton.isEnabled = false
        loginButton.text = "Please wait..."
    }

    private fun hideLoading() {
        loginButton.isEnabled = true
        loginButton.text = "LOG IN"
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        Log.d(TAG, "IS_CONNECTED: $isConnected")
    }
}