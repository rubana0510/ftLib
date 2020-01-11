package com.feedbacktower.ui.payment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.PlanPaymentTransaction
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.ui.plans.SubscriptionPlansScreen
import com.feedbacktower.ui.splash.SplashScreen
import com.feedbacktower.util.*
import kotlinx.android.synthetic.main.activity_plan_payment_success_screen.*
import kotlinx.android.synthetic.main.dialog_referral_success.view.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class PlanPaymentResultScreen : BaseViewActivityImpl(), PaymentResultContract.View {
    companion object {
        const val PAYMENT_SUMMARY = "PAYMENT_SUMMARY"
    }

    @Inject
    lateinit var appPrefs: ApplicationPreferences
    @Inject
    lateinit var presenter: PaymentResultPresenter

    private lateinit var paymentSummary: PaymentSummary
    private var paymentStatus: PlanPaymentTransaction.Status? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_payment_success_screen)
        (application as App).appComponent.paymentComponent().create().inject(this)
        presenter.attachView(this)
        intent?.let {
            paymentSummary =
                it.getSerializableExtra(PAYMENT_SUMMARY) as? PaymentSummary
                    ?: throw IllegalStateException("Payment summary cannot be null")
            transactionId.text = paymentSummary.txid
            date.text = paymentSummary.createdAt.toDate()
            presenter.saveTransactionStatus(paymentSummary)
        }

        button.setOnClickListener {
            if (paymentStatus == null) {
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

    override fun showProgress() {
        super.showProgress()
        showLoading()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        hideLoading()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error occurred")
        builder.setMessage(error.message)
        builder.setPositiveButton("TRY AGAIN") { _, _ -> presenter.refreshAuthToken() }
        builder.setCancelable(false)
        val alert = builder.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
        toast(error.message)
    }


    override fun saveTransactionStatusSuccess() {
        presenter.checkPaymentStatus(paymentSummary.id)
    }

    override fun checkPaymentStatusSuccess(transaction: PlanPaymentTransaction) {
        paymentStatus = transaction.paymentStatus
        if (paymentStatus == PlanPaymentTransaction.Status.SUCCESS) {
            //Success yay!
            setPaymentSuccess(transaction)
        } else if (paymentStatus == PlanPaymentTransaction.Status.PENDING) {
            statusMessage.text = "Please wait, payment status is pending"
            Handler().postDelayed({
                if (presenter.statusCallCount < Constants.PAYMENT_STATUS_CHECK_MAX_WAIT_TIME / Constants.PAYMENT_STATUS_CHECK_INTERVAL)
                    presenter.checkPaymentStatus(paymentSummary.id)
                else
                    setPaymentPending()
            }, Constants.PAYMENT_STATUS_CHECK_INTERVAL)

        } else if (paymentStatus == PlanPaymentTransaction.Status.FAILURE) {
            setPaymentFailed()
        } else {
            toast("Some error occurred")
        }
    }


    override fun refreshAuthTokenSuccess() {
        initializeReferral()
    }

    override fun hideVerifyReferralProgress() {
        button.enable()
    }

    override fun showVerifyReferralProgress() {
        referralCodeInput.disable()
        button.disable()
    }

    override fun verifyReferralCodeSuccess() {
        apply.text = "APPLIED"
        referralCodeLay.disable()
        apply.disable()
        referralCode.disable()
        apply.icon = getDrawable(R.drawable.ic_check_circle_black_24dp)
        apply.iconSize = 16
        apply.iconTint = ContextCompat.getColorStateList(this, R.color.button_green)
        showCodeAppliedMessage()
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
        appPrefs.summary = null
        presenter.refreshAuthToken()
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
            presenter.verifyReferralCode(code)
        }
    }

    private fun showCodeAppliedMessage() {
        val builder = AlertDialog.Builder(this)
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

    private fun openSplash() {
        launchActivity<SplashScreen> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        finish()
    }

    private fun setPaymentPending() {
        message.text = "Payment Pending.."
        image.setImageResource(R.drawable.ic_payment_pending)
        button.text = "OKAY"
        appPrefs.summary = null
        hideLoading()
    }

    private fun setPaymentFailed() {
        message.text = "Payment Failed"
        image.setImageResource(R.drawable.ic_cancel_failure)
        button.text = "TRY AGAIN"
        appPrefs.summary = null
        hideLoading()
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
