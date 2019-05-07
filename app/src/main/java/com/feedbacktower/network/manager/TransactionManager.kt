package com.feedbacktower.network.manager

import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TransactionManager {
    private val TAG = "TransactionManager"
    private val apiService: ApiServiceDescriptor by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instance: TransactionManager? = null

        fun getInstance(): TransactionManager =
            instance ?: synchronized(this) {
                instance ?: TransactionManager().also { instance = it }
            }
    }

    fun generateHash(
        params: GenerateHashRequest,
        onComplete: (GenerateHashResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.generateHashAsync(params).makeRequest(onComplete)
        }
    }

    fun saveResponse(
        params: TransactionResponse,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.saveTransactionResponseAsync(params).makeRequest(onComplete)
        }
    }
}