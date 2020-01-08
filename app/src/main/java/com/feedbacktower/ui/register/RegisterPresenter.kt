package com.feedbacktower.ui.register

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegisterPresenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<RegisterContract.View>(), RegisterContract.Presenter {
    override fun preRegister(phone: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.loginAsync(hashMapOf("phone" to phone)).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onPreRegisterSuccess()
        }
    }

    override fun requestOtp(phone: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.requestOtpAsync(hashMapOf("phone" to phone)).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onRequestOtpSuccess()
        }
    }

    override fun verifyOtp(phone: String, otp: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response =
                apiService.verifyOtpRegisterAsync(hashMapOf("phone" to phone, "otp" to otp)).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            appPrefs.authToken = response.payload?.token
            view?.onVerifyOtpSuccess()
        }
    }

    override fun registerPhone(phone: String, password: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response =
                apiService.registerPhoneAsync(hashMapOf("phone" to phone, "password" to password))
                    .awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            response.payload?.let { data ->
                appPrefs.apply {
                    user = data.user
                    authToken = data.token
                }
            }
            view?.onRegisterPhoneSuccess()
        }
    }

    override fun resetPassword(phone: String, password: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response =
                apiService.resetPasswordAsync(hashMapOf("phone" to phone, "password" to password)).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onResetPasswordSuccess()
        }
    }
}