package com.feedbacktower.ui.qrtransfer.receiver.wait

import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class ReceiverWaitPresenter : BasePresenterImpl<ReceiverWaitContract.View>(),
    ReceiverWaitContract.Presenter {
    override fun listenForChanges(txId: String) {
        getView()?.onListenShowProgress()
        QRTransactionManager.getInstance()
            .checkStatusReceiver(txId) { response, error ->
                getView()?.onListenHideProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@checkStatusReceiver
                }
                getView()?.onListenResponse(response)
            }
    }

    override fun sendPaymentRequest(txId: String, amount: Double) {
        getView()?.onPaymentRequestShowProgress()
        QRTransactionManager.getInstance()
            .requestPayment(txId, amount) { response, error ->
                getView()?.onPaymentRequestHideProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@requestPayment
                }
                getView()?.onPaymentRequestResponse(response)
            }
    }
}