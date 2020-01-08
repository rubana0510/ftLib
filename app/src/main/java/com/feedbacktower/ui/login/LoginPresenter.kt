package com.feedbacktower.ui.login

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<LoginContract.View>(), LoginContract.Presenter {
    override fun login(phone: String, password: String) {
        GlobalScope.launch(Dispatchers.Main) {
           val response =  apiService.loginAsync(hashMapOf("phone" to phone, "password" to password)).awaitNetworkRequest()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            response.payload?.let { payload->
                appPrefs.apply {
                    user = payload.user
                    authToken = payload.token
                }
                view?.onLoginSuccess(payload.user)
            }
        }
    }
}