package com.feedbacktower.network.manager

import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QRTransactionManager {
    private val TAG = "QRTransactionManager"
    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instance: QRTransactionManager? = null

        fun getInstance(): QRTransactionManager =
            instance ?: synchronized(this) {
                instance ?: QRTransactionManager().also { instance = it }
            }
    }

    fun generate(
        onComplete: (QrPaymentStatus?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.generateQrCodeAsync().makeRequest(onComplete)
        }
    }

    fun scan(
        data: String,
        onComplete: (ScanQrResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.scanQrCodeAsync(hashMapOf("code" to data)).makeRequest(onComplete)
        }
    }

    fun cancel(
        data: String,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.cancelWalletPaymentAsync(hashMapOf("code" to data)).makeRequest(onComplete)
        }
    }

    fun checkStatusSender(
        data: String,
        onComplete: (QrStatusSenderResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.checkQrTransferStatusSenderAsync(hashMapOf("code" to data)).makeRequest(onComplete)
        }
    }

    fun checkStatusReceiver(
        data: String,
        onComplete: (QrStatusRecieverResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.checkQrTransferStatusReceiverAsync(hashMapOf("code" to data)).makeRequest(onComplete)
        }
    }

    fun requestPayment(
        code: String, amount: Double,
        onComplete: (QrPaymentStatus?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.requestWalletPaymentAsync(hashMapOf("code" to code, "amount" to amount)).makeRequest(onComplete)
        }
    }

    fun confirmPayment(
        code: String, approved: Boolean,
        onComplete: (QrPaymentStatus?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.confirmWalletPaymentAsync(
                hashMapOf("code" to code,"approved" to approved)
            ).makeRequest(onComplete)
        }
    }

    fun getTransactions(
        timestamp: String,
        onComplete: (QrTransactionsResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getQrTransactionsAsync(timestamp).makeRequest(onComplete)
        }
    }

}