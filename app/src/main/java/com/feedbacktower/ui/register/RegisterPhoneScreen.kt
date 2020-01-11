package com.feedbacktower.ui.register

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.feedbacktower.R
import com.feedbacktower.databinding.ActivityRegisterPhoneScreenBinding
import com.feedbacktower.util.Constants
import com.feedbacktower.util.gone
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.activity_register_phone_screen.*
import org.jetbrains.anko.toast
import android.content.Intent
import com.feedbacktower.App
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.ui.profile.ProfileSetupScreen
import javax.inject.Inject


class RegisterPhoneScreen : BaseViewActivityImpl(), RegisterContract.View {

    companion object {
        const val SCREEN_TYPE_KEY = "SCREEN_TYPE_KEY"
    }

    @Inject
    lateinit var presenter: RegisterPresenter
    private lateinit var binding: ActivityRegisterPhoneScreenBinding

    private enum class State { INITIAL, OTP_SENT, OTP_VERIFIED, REGISTERED, RESET }
    enum class ScreenFunction { FORGOT_PASSWORD, REGISTER }

    private var state = State.INITIAL
    private lateinit var screenFunction: ScreenFunction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).appComponent.authComponent().create().inject(this)
        presenter.attachView(this)
        screenFunction = intent?.getSerializableExtra(SCREEN_TYPE_KEY) as? ScreenFunction
            ?: throw IllegalArgumentException("No type args passed")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_phone_screen)
        initUi()
    }

    private fun initUi() {
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
                        presenter.preRegister(phone)
                    else
                        presenter.requestOtp(phone)
                }
            }
            State.OTP_SENT -> {
                val phone = mobileInput.text.toString().trim()
                val otp = OTPInput.text.toString().trim()
                if (phoneValid(phone) && otpValid(otp)) {
                    presenter.verifyOtp(phone, otp)
                }
            }
            State.OTP_VERIFIED -> {
                val password = passwordInput.text.toString().trim()
                val phone = mobileInput.text.toString().trim()
                if (passwordValid(password)) {
                    if (screenFunction == ScreenFunction.REGISTER)
                        presenter.registerPhone(phone, password)
                    else
                        presenter.resetPassword(phone, password)
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

    override fun showProgress() {
        super.showProgress()
        showLoading()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        hideLoading()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        toast(error.message)
    }

    override fun onPreRegisterSuccess() {
        state = State.OTP_SENT
        toast("OTP sent to your Phone")
        updateUi()
    }

    override fun onRequestOtpSuccess() {
        state = State.OTP_SENT
        toast("OTP sent to your Phone")
        updateUi()
    }

    override fun onVerifyOtpSuccess() {
        state = State.OTP_VERIFIED
        toast("OTP verified")
        updateUi()
    }

    override fun onRegisterPhoneSuccess() {
        state = State.REGISTERED
        launchActivity<ProfileSetupScreen> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        toast("Registered successfully")
        updateUi()
    }

    override fun onResetPasswordSuccess() {
        state = State.RESET
        toast("Password has been set,  please login")
        updateUi()
        finish()
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

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }

}
