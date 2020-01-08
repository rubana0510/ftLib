package com.feedbacktower.ui.payment

import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.PlanPaymentTransaction
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface PaymentResultContract {
    interface View : BaseView {
        fun verifyReferralCodeSuccess()
        fun refreshAuthTokenSuccess()
        fun checkPaymentStatusSuccess(transaction: PlanPaymentTransaction)
        fun saveTransactionStatusSuccess()

        fun showVerifyReferralProgress()
        fun hideVerifyReferralProgress()

    }

    interface Presenter : BasePresenter<View> {
        fun verifyReferralCode(code: String)
        fun refreshAuthToken()
        fun checkPaymentStatus(transactionId: String)
        fun saveTransactionStatus(summary: PaymentSummary)
    }
}