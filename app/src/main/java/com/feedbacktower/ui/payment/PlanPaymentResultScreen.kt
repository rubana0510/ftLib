package com.feedbacktower.ui.payment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.R
import com.feedbacktower.data.models.PayUResponse
import com.feedbacktower.data.models.PlanPaymentTransaction
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.TransactionManager
import com.feedbacktower.network.models.GenerateHashResponse
import com.feedbacktower.ui.SplashScreen
import com.feedbacktower.util.*
import com.feedbacktower.util.toDate
import com.google.gson.Gson
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import kotlinx.android.synthetic.main.activity_plan_payment_success_screen.*
import org.jetbrains.anko.toast

class PlanPaymentResultScreen : AppCompatActivity() {
    companion object {
        const val TRANSACTION = "TRANSACTION"
    }

    private var paymentStatus: PlanPaymentTransaction.Status? = null
    private var statusCallCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_payment_success_screen)

        intent?.let {
            val transactionResponse: TransactionResponse =
                it.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE)
                    ?: throw IllegalStateException("TransactionResponse cannot be null")
            val generateHashResponse: GenerateHashResponse =
                it.getSerializableExtra(TRANSACTION) as? GenerateHashResponse
                    ?: throw IllegalStateException("Transaction cannot be null")

            val payuResponse = transactionResponse.getPayuResponse()
            val merchantResponse = transactionResponse.transactionDetails
            val payUResponse: PayUResponse? =
                Gson().fromJson(payuResponse, PayUResponse::class.java)
            transactionId.text = generateHashResponse.txn.txnid
            date.text = generateHashResponse.txn.createdAt.toDate()
            if (transactionResponse.transactionStatus == TransactionResponse.TransactionStatus.SUCCESSFUL) {
                //payment success-> check for server status
                checkTransactionStatus(generateHashResponse.txn.id)
            } else {
                setPaymentFailed()
            }
        }



        button.setOnClickListener {
            if (paymentStatus == PlanPaymentTransaction.Status.SUCCESS) {
                launchActivity<SplashScreen> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                finish()
            } else if (paymentStatus == PlanPaymentTransaction.Status.FAILURE) {
                launchActivity<SplashScreen> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                finish()
            } else if (paymentStatus == PlanPaymentTransaction.Status.PENDING) {
                launchActivity<SplashScreen> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                finish()
            }
        }
        //getPlanTransactions()
    }

    private fun checkTransactionStatus(id: String) {
        showLoading()
        statusCallCount++
        TransactionManager.getInstance()
            .checkPaymentStatus(id) { response, error ->
                if (error == null) {
                    response?.let {
                        paymentStatus = it.transaction.PaymentStatus
                        if (it.transaction.PaymentStatus == PlanPaymentTransaction.Status.SUCCESS) {
                            //Success yay!

                            setPaymentSuccess()

                        } else if (it.transaction.PaymentStatus == PlanPaymentTransaction.Status.PENDING) {
                            statusMessage.text = "Please wait, payment status is pending"
                            Handler().postDelayed({
                                if (statusCallCount < Constants.PAYMENT_STATUS_CHECK_MAX_WAIT_TIME / Constants.PAYMENT_STATUS_CHECK_INTERVAL)
                                    checkTransactionStatus(id)
                                else
                                    setPaymentPending()
                            }, Constants.PAYMENT_STATUS_CHECK_INTERVAL)

                        } else {
                            setPaymentFailed()
                        }
                    }
                } else {
                    setPaymentFailed()
                }
            }
    }

    private fun hideLoading() {
        loading.gone()
        content.visible()
    }

    private fun showLoading() {
        loading.visible()
        content.gone()
    }

    private fun setPaymentSuccess() {
        message.text = "Payment Successful"
        image.setImageResource(R.drawable.ic_success_check)
        button.text = "GO TO DASHBOARD"
        initializeReferral()
        hideLoading()
    }

    private fun initializeReferral() {
        referralCodeLay.visible()
        apply.setOnClickListener {
            val code = referralCode.text.toString().trim()
            if (code.isEmpty()) {
                referralCode.requestFocus()
                toast("Enter  referral code")
                return@setOnClickListener
            }
            verifyReferralCode(code)
        }
    }

    private fun verifyReferralCode(code: String) {
        referralCodeLay.disable()
        ProfileManager.getInstance()
            .applyReferralCode(code) { _, error ->
                if (error == null) {
                    apply.text = "APPLIED"
                    toast("Applied successfully")
                } else {
                    referralCodeLay.disable()
                    toast(error.message ?: getString(R.string.default_err_message))
                }
            }
    }

    private fun setPaymentPending() {
        message.text = "Payment Pending.."
        image.setImageResource(R.drawable.ic_payment_pending)
        button.text = "OKAY"
        hideLoading()
    }

    private fun setPaymentFailed() {
        message.text = "Payment Failed"
        image.setImageResource(R.drawable.ic_cancel_failure)
        button.text = "TRY AGAIN"
        hideLoading()
    }

    private fun getPlanTransactions() {
        txDetails.gone()
        TransactionManager.getInstance()
            .getTransactions { response, error ->
                if (error == null && response?.transactions != null) {
                    if (response.transactions.isNotEmpty()) {
                        txDetails.visible()
                        val transaction = response.transactions[0]
                        transactionId.text = transaction.txnid
                        date.text = transaction.updatedAt.toDate()
                    } else {
                        //toast("You don't have any active plans")
                        Log.e("PlanPaySuccess", "No transaction data")
                    }
                }
            }
    }


}
