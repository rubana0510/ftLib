package com.feedbacktower.ui.qrtransfer.sender.wait

import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class SenderWaitPresenter : BasePresenterImpl<SenderWaitContract.View>(),
    SenderWaitContract.Presenter {
    override fun acceptRequest(txId: String) {
        getView()?.showProgress()
        QRTransactionManager.getInstance()
            .confirmPayment(txId, true) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@confirmPayment
                }
                getView()?.onRequestAccepted(response)
            }
    }

    override fun cancelTransaction(txId: String) {
        getView()?.showProgress()
        QRTransactionManager.getInstance()
            .cancel(txId) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@cancel
                }
                getView()?.onTransactionCancelled(response)
            }
    }

    override fun listenForChanges(txId: String) {
        getView()?.showProgress()
        QRTransactionManager.getInstance()
            .checkStatusSender(txId) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@checkStatusSender
                }
                getView()?.onQrPaymentStatusResponse(response)
            }
    }
}