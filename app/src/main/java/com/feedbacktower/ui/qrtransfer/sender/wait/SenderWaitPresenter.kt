package com.feedbacktower.ui.qrtransfer.sender.wait

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SenderWaitPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<SenderWaitContract.View>(),
    SenderWaitContract.Presenter {
    override fun acceptRequest(txId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.confirmWalletPaymentAsync(hashMapOf("code" to txId, "approved" to true))
                .awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onRequestAccepted(response.payload)
        }
    }

    override fun cancelTransaction(txId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.cancelWalletPaymentAsync(hashMapOf("code" to txId))
                .awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onTransactionCancelled(response.payload)
        }
    }

    override fun listenForChanges(txId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.checkQrTransferStatusSenderAsync(hashMapOf("code" to txId))
                .awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onQrPaymentStatusResponse(response.payload)
        }
    }
}