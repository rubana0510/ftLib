package com.feedbacktower.ui.qrtransfer.sender.showqr

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SenderQrPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<SenderQrContract.View>(),
    SenderQrContract.Presenter {

    override fun fetchQrData() {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showQrDataFetchProgress()
            val response = apiService.generateQrCodeAsync().awaitNetworkRequest()
            view?.hideQrDataFetchProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onQrDataFetched(response.payload)
        }
    }

    override fun listenForChanges(txId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showQrPaymentStatusProgress()
            val response = apiService.checkQrTransferStatusSenderAsync(hashMapOf("code" to txId)).awaitNetworkRequest()
          //  view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onQrPaymentStatusResponse(response.payload)
        }
    }
}