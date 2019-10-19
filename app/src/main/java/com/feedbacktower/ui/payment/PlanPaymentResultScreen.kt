package com.feedbacktower.ui.payment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.PlanPaymentTransaction
import com.feedbacktower.network.manager.AuthManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.TransactionManager
import com.feedbacktower.ui.splash.SplashScreen
import com.feedbacktower.ui.plans.SubscriptionPlansScreen
import com.feedbacktower.util.*
import com.feedbacktower.util.toDate
import kotlinx.android.synthetic.main.activity_plan_payment_success_screen.*
import kotlinx.android.synthetic.main.dialog_referral_success.view.*
import org.jetbrains.anko.toast

class PlanPaymentResultScreen : AppCompatActivity() {
    companion object {
        const val PAYMENT_SUMMARY = "PAYMENT_SUMMARY"
    }

    private lateinit var paymentSummary: PaymentSummary
    private var statusCallCount = 0
    private var paymentStatus: PlanPaymentTransaction.Status? = null
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
            if(paymentStatus == null){
                launchActivity<SubscriptionPlansScreen> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                finish()
                return@setOnClickListener
            }

            if (paymentStatus == PlanPaymentTransaction.Status.SUCCESS) {
                launchActivity<SplashScreen> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                finish()
            } else if (paymentStatus == PlanPaymentTransaction.Status.FAILURE) {
                finish()
            } else if (paymentStatus == PlanPaymentTransaction.Status.PENDING) {
                //TODO: needs to be handled payment pending state
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

                            setPaymentSuccess(it.transaction)

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
                if (
                    error.message == "USER_NOT_FOUND"
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

    private fun setPaymentSuccess(transaction: PlanPaymentTransaction) {
        message.text = "Payment Successful"
        image.setImageResource(R.drawable.ic_success_check)
        button.text = "GO TO DASHBOARD"
        referralNote.text = "Apply Referral code and get \n Rs.${transaction.amount} more in wallet"
        walletBalance.text = "Rs.${transaction.amount}"
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
        referralCodeInput.disable()
        button.disable()
        ProfileManager.getInstance()
            .applyReferralCode(code) { _, error ->
                button.enable()
                if (error == null) {
                    apply.text = "APPLIED"
                    referralCodeLay.disable()
                    apply.disable()
                    referralCode.disable()
                    apply.icon = getDrawable(R.drawable.ic_check_circle_black_24dp)
                    apply.iconSize = 16
                    apply.iconTint = ContextCompat.getColorStateList(this, R.color.button_green)
                    showCodeAppliedMessage()
                } else {
                    toast("Referral code invalid")
                }
            }
    }

    private fun showCodeAppliedMessage() {
        val builder= AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_referral_success, null)
        val alert = builder.create()
        alert.setView(view)
        view.actionButton.setOnClickListener {
            alert.dismiss()
            openSplash()
        }
        alert.setOnDismissListener {
            openSplash()
        }
        alert.show()
    }

    private fun openSplash(){
        launchActivity<SplashScreen> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        finish()
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
