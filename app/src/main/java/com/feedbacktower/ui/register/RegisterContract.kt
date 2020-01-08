package com.feedbacktower.ui.register

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface RegisterContract {
    interface View : BaseView {
        fun onPreRegisterSuccess()
        fun onRequestOtpSuccess()
        fun onVerifyOtpSuccess()
        fun onRegisterPhoneSuccess()
        fun onResetPasswordSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun preRegister(phone: String)
        fun requestOtp(phone: String)
        fun verifyOtp(phone: String, otp: String)
        fun registerPhone(phone: String, password: String)
        fun resetPassword(phone: String, password: String)
    }
}