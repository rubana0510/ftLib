package com.feedbacktower.network.manager

import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OtpManager {
    private val TAG = "OtpManager"
    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instance: OtpManager? = null

        fun getInstance(): OtpManager =
            instance ?: synchronized(this) {
                instance ?: OtpManager().also { instance = it }
            }
    }

    fun sendOtp(
        params: GenerateHashRequest,
        onComplete: (GenerateHashResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.generateHashAsync(params).makeRequest(onComplete)
        }
    }
}