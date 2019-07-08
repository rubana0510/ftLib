package com.feedbacktower.network.manager

import com.feedbacktower.data.models.PayUResponse
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.PlanTransaction
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
        summary: PaymentSummary,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.saveTransactionResponseAsync(summary).makeRequest(onComplete)
        }
    }

    fun cancel(
        transactionId: String,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.cancelTransactionAsync(hashMapOf("id" to transactionId)).makeRequest(onComplete)
        }
    }


    fun checkPaymentStatus(
        transactionId: String,
        onComplete: (PaymentTxnResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.checkPaymentStatusAsync(transactionId).makeRequest(onComplete)
        }
    }
    fun getTransactions(
        onComplete: (PlanTransactionsResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getTransactionsAsync().makeRequest(onComplete)
        }
    }
}