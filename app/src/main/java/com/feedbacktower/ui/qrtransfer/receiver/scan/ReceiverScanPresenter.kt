package com.feedbacktower.ui.qrtransfer.receiver.scan

import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class ReceiverScanPresenter : BasePresenterImpl<ReceiverScanContract.View>(),
    ReceiverScanContract.Presenter {
    override fun verifyScannedData(data: String) {
        getView()?.showProgress()
        QRTransactionManager.getInstance()
            .scan(data) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@scan
                }
                getView()?.onVerified(response)
            }
    }
}