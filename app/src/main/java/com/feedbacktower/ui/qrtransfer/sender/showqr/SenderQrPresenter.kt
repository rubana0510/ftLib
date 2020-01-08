package com.feedbacktower.ui.qrtransfer.sender.showqr

import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class SenderQrPresenter : BasePresenterImpl<SenderQrContract.View>(),
    SenderQrContract.Presenter {

    override fun fetchQrData() {
        view?.showProgress()
        QRTransactionManager.getInstance()
            .generate { response, error ->
                view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@generate
                }
                view?.onQrDataFetched(response)
            }
    }

    override fun listenForChanges(txId: String) {
        QRTransactionManager.getInstance()
            .checkStatusSender(txId) { response, error ->
             //   view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@checkStatusSender
                }
                view?.onQrPaymentStatusResponse(response)
            }
    }
}