package com.feedbacktower.ui.qrtransfer.sender.wait

import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class SenderWaitPresenter : BasePresenterImpl<SenderWaitContract.View>(),
    SenderWaitContract.Presenter {
    override fun acceptRequest(txId: String) {
        view?.showProgress()
        QRTransactionManager.getInstance()
            .confirmPayment(txId, true) { response, error ->
                view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@confirmPayment
                }
                view?.onRequestAccepted(response)
            }
    }

    override fun cancelTransaction(txId: String) {
        view?.showProgress()
        QRTransactionManager.getInstance()
            .cancel(txId) { response, error ->
                view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@cancel
                }
                view?.onTransactionCancelled(response)
            }
    }

    override fun listenForChanges(txId: String) {
        view?.showProgress()
        QRTransactionManager.getInstance()
            .checkStatusSender(txId) { response, error ->
                view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@checkStatusSender
                }
                view?.onQrPaymentStatusResponse(response)
            }
    }
}