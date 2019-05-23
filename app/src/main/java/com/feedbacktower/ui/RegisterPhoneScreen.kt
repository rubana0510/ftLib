package com.feedbacktower.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.ActivityRegisterPhoneScreenBinding
import com.feedbacktower.network.manager.AuthManager
import com.feedbacktower.util.Constants
import com.feedbacktower.util.gone
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.activity_register_phone_screen.*
import org.jetbrains.anko.toast
import android.content.Intent


class RegisterPhoneScreen : AppCompatActivity() {
    companion object {
        const val SCREEN_TYPE_KEY = "SCREEN_TYPE_KEY"
    }

    private enum class State { INITIAL, OTP_SENT, OTP_VERIFIED, REGISTERED, RESET }
    enum class ScreenFunction { FORGOT_PASSWORD, REGISTER }

    private var state = State.INITIAL
    private lateinit var screenFunction: ScreenFunction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenFunction = intent?.getSerializableExtra(SCREEN_TYPE_KEY) as? ScreenFunction
            ?: throw IllegalArgumentException("No type args passed")
        val binding: ActivityRegisterPhoneScreenBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_register_phone_screen)
        initUi(binding)
    }

    private fun initUi(binding: ActivityRegisterPhoneScreenBinding) {
        binding.onSignUpClicked = onSignUpClicked
        binding.pageTitle = if (screenFunction == ScreenFunction.FORGOT_PASSWORD) "FORGOT PASSWORD" else "SIGN UP"
        binding.textInputPassword.hint = "New Password"
        updateUi()
    }

    private val onSignUpClicked = View.OnClickListener {
        when (state) {
            State.INITIAL -> {
                val phone = mobileInput.text.toString().trim()
                if (phoneValid(phone)) {
                    if (screenFunction == ScreenFunction.REGISTER)
                        preRegister(phone)
                    else
                        requestOtp(phone)
                }
            }
            State.OTP_SENT -> {
                val phone = mobileInput.text.toString().trim()
                val otp = OTPInput.text.toString().trim()
                if (phoneValid(phone) && otpValid(otp)) {
                    verifyOtp(phone, otp)
                }
            }
            State.OTP_VERIFIED -> {
                val password = passwordInput.text.toString().trim()
                val phone = mobileInput.text.toString().trim()
                if (passwordValid(password)) {
                    if (screenFunction == ScreenFunction.REGISTER)
                        registerPhone(phone, password)
                    else
                        resetPassword(phone, password)
                }
            }
            else -> {
                finish()
            }
        }
    }


    private fun updateUi() {
        when (state) {
            State.INITIAL -> {
                textInputPhone.visible()
                textInputPhone.isEnabled = true
                textInputOtp.gone()
                textInputPassword.gone()
                textInputPhone.requestFocus()
            }
            State.OTP_SENT -> {
                textInputPhone.visible()
                textInputPhone.isEnabled = false
                textInputOtp.visible()
                textInputOtp.requestFocus()
                textInputPassword.gone()
            }
            State.OTP_VERIFIED -> {
                textInputPhone.visible()
                textInputPhone.isEnabled = false
                textInputOtp.isEnabled = false
                textInputOtp.visible()
                textInputPassword.visible()
                textInputPassword.requestFocus()
            }
            else -> {

            }
        }
    }


    private fun phoneValid(phone: String): Boolean {
        textInputPhone.error = null
        return when {
            phone.isEmpty() || phone.length != Constants.PHONE_LENGTH -> {
                textInputPhone.error = "Enter valid phone number"
                false
            }
            else -> true
        }
    }

    private fun preRegister(phone: String) {
        showLoading()
        AuthManager.getInstance().preRegister(phone)
        preReg@{ response, error ->
            if (error != null) {
                toast(error.message ?: getString(R.string.default_err_message))
                hideLoading()
                return@preReg
            }
            if (response != null) {
                //  OTP_RECEIVED = response.user.otp
                state = State.OTP_SENT
                toast("OTP sent to your Phone")
            } else {
                toast("Unknown error occurred")
            }
            hideLoading()
            updateUi()
        }
    }

    private fun requestOtp(phone: String) {
        showLoading()
        AuthManager.getInstance().requestOtp(phone)
        reqOtp@{ response, error ->
            if (error != null) {
                toast(error.message ?: getString(R.string.default_err_message))
                hideLoading()
                return@reqOtp
            }
            if (response != null) {
                //  OTP_RECEIVED = response.user.otp
                state = State.OTP_SENT
                toast("OTP sent to your Phone")
            } else {
                toast("Unknown error occurred")
            }
            hideLoading()
            updateUi()
        }
    }

    private fun verifyOtp(phone: String, otp: String) {
        showLoading()
        AuthManager.getInstance().verifyOtpRegister(phone, otp)
        { response, error ->
            if (error != null) {
                toast(error.message ?: getString(R.string.default_err_message))
                hideLoading()
                return@verifyOtpRegister
            }
            if (response != null) {
                AppPrefs.getInstance(this).authToken = response.token
                state = State.OTP_VERIFIED
                toast("OTP verified")
            } else {
                toast("Unknown error occurred")
            }
            hideLoading()
            updateUi()
        }
    }

    private fun registerPhone(phone: String, password: String) {
        showLoading()
        AuthManager.getInstance().registerPhone(phone, password)
        rp@{ response, error ->
            if (error != null) {
                toast(error.message ?: getString(R.string.default_err_message))
                hideLoading()
                return@rp
            }
            if (response != null) {
                state = State.REGISTERED
                AppPrefs.getInstance(this).apply {
                    user = response.user
                    authToken = response.token
                }
                launchActivity<ProfileSetupScreen> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                toast("Registered")
            } else {
                toast("Unknown error occurred")
            }
            hideLoading()
            updateUi()
        }
    }

    private fun resetPassword(phone: String, password: String) {
        showLoading()
        AuthManager.getInstance().resetPassword(phone, password)
        rp@{ response, error ->
            if (error != null) {
                toast(error.message ?: getString(R.string.default_err_message))
                hideLoading()
                return@rp
            }

            if (response != null) {
                state = State.RESET
                toast("Password has been set,  please login")
                finish()
            } else {
                toast("Unknown error occurred")
            }
            hideLoading()
            updateUi()
        }
    }

    private fun showLoading() {
        registerButton.isEnabled = false
        registerButton.text = getString(R.string.please_wait)
    }

    private fun hideLoading() {
        registerButton.isEnabled = true
        registerButton.text = getButtonText()
    }

    private fun getButtonText(): CharSequence {
        return when (state) {
            State.INITIAL -> getString(R.string.send_otp)
            State.OTP_SENT -> getString(R.string.verify_otp)
            State.OTP_VERIFIED -> if (screenFunction == ScreenFunction.REGISTER) getString(R.string.register) else getString(
                R.string.reset
            )
            State.REGISTERED -> getString(R.string.go_back)
            State.RESET -> getString(R.string.go_back)
        }
    }

    private fun otpValid(otp: String): Boolean {
        textInputOtp.error = null
        return when {
            otp.isEmpty() || otp.length != Constants.OTP_LENGTH -> {
                textInputOtp.error = "Enter valid OTP"
                false
            }
            else -> true
        }
    }

    private fun passwordValid(password: String): Boolean {
        textInputPassword.error = null
        return when {
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

}
