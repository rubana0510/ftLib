package com.feedbacktower.network.manager

import com.feedbacktower.data.models.User
import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthManager {
    private val TAG = "AuthManager"
    private val apiService: ApiServiceDescriptor by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var authManager: AuthManager? = null

        fun getInstance(): AuthManager =
            authManager ?: synchronized(this) {
                authManager ?: AuthManager().also { authManager = it }
            }
    }

    fun phoneLogin(
        phone: String,
        password: String,
        onComplete: (AuthResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.loginAsync(hashMapOf("phone" to phone, "password" to password))
                .makeRequest(onComplete)
        }
    }

    fun preRegister(
        phone: String,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.preRegisterAsync(hashMapOf("phone" to phone))
                .makeRequest(onComplete)
        }
    }

    fun verifyOtpRegister(
        phone: String,
        otp: String,
        onComplete: (TokenRes?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.verifyOtpRegisterAsync(hashMapOf("phone" to phone, "otp" to otp))
                .makeRequest(onComplete)
        }
    }

    fun registerPhone(
        phone: String,
        password: String,
        onComplete: (AuthResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.registerPhoneAsync(hashMapOf("phone" to phone, "password" to password))
                .makeRequest(onComplete)
        }
    }

    fun registerAsBusiness(
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.registerAsBusinessAsync()
                .makeRequest(onComplete)
        }
    }

}