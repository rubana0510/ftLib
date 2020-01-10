package com.feedbacktower.ui.qrtransfer.receiver.wait

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceiverWaitPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<ReceiverWaitContract.View>(),
    ReceiverWaitContract.Presenter {
    override fun listenForChanges(txId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.onListenShowProgress()
            val response =
                apiService.checkQrTransferStatusReceiverAsync(hashMapOf("code" to txId)).awaitNetworkRequest()
            view?.onListenHideProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onListenResponse(response.payload)
        }
    }

    override fun sendPaymentRequest(txId: String, amount: Double) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.onPaymentRequestShowProgress()
            val response =
                apiService.requestWalletPaymentAsync(hashMapOf("code" to txId, "amount" to amount))
                    .awaitNetworkRequest()
            view?.onPaymentRequestHideProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onPaymentRequestResponse(response.payload)
        }
    }
}