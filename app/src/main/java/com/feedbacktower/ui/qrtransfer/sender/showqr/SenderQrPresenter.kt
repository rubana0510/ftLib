package com.feedbacktower.ui.qrtransfer.sender.showqr

import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class SenderQrPresenter : BasePresenterImpl<SenderQrContract.View>(),
    SenderQrContract.Presenter {

    override fun fetchQrData() {
        getView()?.showProgress()
        QRTransactionManager.getInstance()
            .generate { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@generate
                }
                getView()?.onQrDataFetched(response)
            }
    }

    override fun listenForChanges(txId: String) {
        QRTransactionManager.getInstance()
            .checkStatusSender(txId) { response, error ->
             //   getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@checkStatusSender
                }
                getView()?.onQrPaymentStatusResponse(response)
            }
    }
}