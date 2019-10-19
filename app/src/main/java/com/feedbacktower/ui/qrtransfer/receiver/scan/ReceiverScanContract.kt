package com.feedbacktower.ui.qrtransfer.receiver.scan

import com.feedbacktower.network.models.ScanQrResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface ReceiverScanContract {
    interface View : BaseView {
        fun onVerified(response: ScanQrResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun verifyScannedData(data: String)
    }
}