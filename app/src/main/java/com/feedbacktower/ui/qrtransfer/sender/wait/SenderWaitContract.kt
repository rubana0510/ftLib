package com.feedbacktower.ui.qrtransfer.sender.wait

import com.feedbacktower.network.models.EmptyResponse
import com.feedbacktower.network.models.QrPaymentStatus
import com.feedbacktower.network.models.QrStatusSenderResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface SenderWaitContract {
    interface View : BaseView {
        fun onQrPaymentStatusResponse(response: QrStatusSenderResponse?)
        fun onRequestAccepted(response: QrPaymentStatus?)
        fun onTransactionCancelled(response: EmptyResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun acceptRequest(txId: String)
        fun cancelTransaction(txId: String)
        fun listenForChanges(txId: String)
    }
}