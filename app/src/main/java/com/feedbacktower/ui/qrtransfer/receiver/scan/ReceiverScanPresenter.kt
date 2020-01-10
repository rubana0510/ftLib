package com.feedbacktower.ui.qrtransfer.receiver.scan

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceiverScanPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<ReceiverScanContract.View>(),
    ReceiverScanContract.Presenter {
    override fun verifyScannedData(data: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.scanQrCodeAsync(hashMapOf("code" to data))
                .awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onVerified(response.payload)
        }
    }
}