package com.feedbacktower.ui.qrtransfer.receiver.scan

import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class ReceiverScanPresenter : BasePresenterImpl<ReceiverScanContract.View>(),
    ReceiverScanContract.Presenter {
    override fun verifyScannedData(data: String) {
        view?.showProgress()
        QRTransactionManager.getInstance()
            .scan(data) { response, error ->
                view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@scan
                }
                view?.onVerified(response)
            }
    }
}