package com.feedbacktower.ui.qrtransfer.receiver.wait

import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class ReceiverWaitPresenter : BasePresenterImpl<ReceiverWaitContract.View>(),
    ReceiverWaitContract.Presenter {
    override fun listenForChanges(txId: String) {
        view?.onListenShowProgress()
        QRTransactionManager.getInstance()
            .checkStatusReceiver(txId) { response, error ->
                view?.onListenHideProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@checkStatusReceiver
                }
                view?.onListenResponse(response)
            }
    }

    override fun sendPaymentRequest(txId: String, amount: Double) {
        view?.onPaymentRequestShowProgress()
        QRTransactionManager.getInstance()
            .requestPayment(txId, amount) { response, error ->
                view?.onPaymentRequestHideProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@requestPayment
                }
                view?.onPaymentRequestResponse(response)
            }
    }
}