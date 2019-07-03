package com.feedbacktower.ui.payment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.PayUResponse
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.PlanPaymentTransaction
import com.feedbacktower.network.manager.AuthManager
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
        const val PAYMENT_SUMMARY = "PAYMENT_SUMMARY"
    }

    private lateinit var paymentSummary: PaymentSummary
    private var statusCallCount = 0
    private lateinit var paymentStatus: PlanPaymentTransaction.Status
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_payment_success_screen)

        intent?.let {
            paymentSummary =
                it.getSerializableExtra(PAYMENT_SUMMARY) as? PaymentSummary
                    ?: throw IllegalStateException("Payment summary cannot be null")
            transactionId.text = paymentSummary.txid
            date.text = paymentSummary.createdAt.toDate()
            saveTransactionStatus()
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

    private fun saveTransactionStatus() {
        showLoading()
        TransactionManager.getInstance()
            .saveResponse(paymentSummary) { _, error ->
                hideLoading()
                if (error == null) {
                    checkTransactionStatus()
                } else {
                    toast(error.message ?: getString(R.string.default_err_message))
                }
            }
    }

    private fun checkTransactionStatus() {
        showLoading()
        statusCallCount++
        TransactionManager.getInstance()
            .checkPaymentStatus(paymentSummary.id) { response, error ->
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
                                    checkTransactionStatus()
                                else
                                    setPaymentPending()
                            }, Constants.PAYMENT_STATUS_CHECK_INTERVAL)

                        } else if (it.transaction.PaymentStatus == PlanPaymentTransaction.Status.FAILURE) {
                            setPaymentFailed()
                        } else {
                            toast("Some error occurred")
                        }
                    }
                } else {
                    toast("Some error occurred")
                }
            }
    }

    private fun refreshAuthToken() {
        AuthManager.getInstance().refreshToken()
        { response, error ->
            if (error != null) {
                //toast(error.message ?: getString(R.string.default_err_message))
                //TODO: Must be handled using the error code  propagated from make request
                if (
                    error.message?.contains("User") == true
                    && error.message?.contains("not") == true
                    && error.message?.contains("found") == true
                ) {
                    logOut()
                    return@refreshToken
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error occurred")
                builder.setMessage(error.message ?: getString(R.string.default_err_message))
                builder.setPositiveButton("TRY AGAIN") { _, _ -> refreshAuthToken() }
                builder.setCancelable(false)
                val alert = builder.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()
                return@refreshToken
            }
            if (response != null) {
                AppPrefs.getInstance(this).apply {
                    user = response.user
                    authToken = response.token
                }
                initializeReferral()
                Log.i("PlanPayment", "Token refreshed")
            } else {
                Log.e("PlanPayment", "Token refresh failed")
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
        AppPrefs.getInstance(this).summary = null
        refreshAuthToken()
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
                    referralCodeLay.disable()
                    apply.disable()
                    referralCode.disable()
                    toast("Applied successfully")
                } else {
                    toast("Referral code invalid")
                }
            }
    }

    private fun setPaymentPending() {
        message.text = "Payment Pending.."
        image.setImageResource(R.drawable.ic_payment_pending)
        button.text = "OKAY"
        AppPrefs.getInstance(this).summary = null
        hideLoading()
    }

    private fun setPaymentFailed() {
        message.text = "Payment Failed"
        image.setImageResource(R.drawable.ic_cancel_failure)
        button.text = "TRY AGAIN"
        AppPrefs.getInstance(this).summary = null
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
