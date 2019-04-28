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
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.activity_register_phone_screen.*
import org.jetbrains.anko.toast

class RegisterPhoneScreen : AppCompatActivity() {
    private enum class State { INITIAL, OTP_SENT, OTP_VERIFIED, REGISTERED }

    private var state = State.INITIAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRegisterPhoneScreenBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_register_phone_screen)
        initUi(binding)
    }

    private fun initUi(binding: ActivityRegisterPhoneScreenBinding) {
        binding.onSignUpClicked = onSignUpClicked
        updateUi()
    }

    private val onSignUpClicked = View.OnClickListener {
        when (state) {
            State.INITIAL -> {
                val phone = mobileInput.text.toString().trim()
                if (phoneValid(phone)) {
                    sentOtp(phone)
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
                    registerPhone(phone, password)
                }
            }
            else -> {

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
                textInputOtp.gone()
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

    private fun sentOtp(phone: String) {
        showLoading()
        AuthManager.getInstance().preRegister(phone)
        { response, error ->
            if (error != null) {
                toast(error.message ?: getString(R.string.default_err_message))
                hideLoading()
                return@preRegister
            }
            response?.let {
                if (response.status) {
                    state = State.OTP_SENT
                    toast("OTP sent to your Phone")
                } else {
                    toast(response.msg)
                }
                hideLoading()
                updateUi()
            }
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
            response?.let {
                if (response.status) {
                    state = State.OTP_VERIFIED
                    AppPrefs.getInstance(this).authToken = it.payload.token
                    toast("OTP verified")
                } else {
                    toast(response.msg)
                }
                hideLoading()
                updateUi()
            }
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
            response?.let {
                if (response.status) {
                    state = State.REGISTERED
                    toast("Registered")
                } else {
                    toast(response.msg)
                }
                hideLoading()
                updateUi()
            }
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
            State.OTP_VERIFIED -> getString(R.string.register)
            State.REGISTERED -> getString(R.string.register)
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
