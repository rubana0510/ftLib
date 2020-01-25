package com.feedbacktower.ui.plans

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.feedbacktower.App
import com.feedbacktower.BuildConfig
import com.feedbacktower.R
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.PayUResponse
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.Plan
import com.feedbacktower.databinding.ActivitySubscriptionPlanScreenBinding
import com.feedbacktower.network.env.Env
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GenerateHashRequest
import com.feedbacktower.network.models.GenerateHashResponse
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.ui.payment.PlanPaymentResultScreen
import com.feedbacktower.ui.splash.SplashScreen
import com.feedbacktower.util.enable
import com.feedbacktower.util.launchActivity
import com.google.gson.Gson
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import com.payumoney.sdkui.ui.utils.ResultModel
import kotlinx.android.synthetic.main.activity_subscription_plan_screen.*
import org.jetbrains.anko.toast
import java.util.*
import javax.inject.Inject


class SubscriptionPlansScreen : BaseViewActivityImpl(), SubscriptionPlansContract.View {
    @Inject
    lateinit var appPrefs: ApplicationPreferences

    @Inject
    lateinit var presenter: SubscriptionPlansPresenter


    private var hashResponse: GenerateHashResponse? = null
    private var plan: Plan? = null
    private lateinit var binding: ActivitySubscriptionPlanScreenBinding
    private val TAG = "SubscriptionPlansScreen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).appComponent.paymentComponent().create().inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription_plan_screen)
        initUi()
        presenter.attachView(this)
    }

    private fun initUi() {

        binding.onContinueClick = onContinueClick

        val pendingPaymentSummary = appPrefs.summary
        if (pendingPaymentSummary != null) {
            launchActivity<PlanPaymentResultScreen> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(PlanPaymentResultScreen.PAYMENT_SUMMARY, pendingPaymentSummary)
            }
            finish()
        } else {
            getPlanList()
        }
    }


    private fun getPlanList() {
        var categoryId = appPrefs.getValue("MASTER_CAT_ID", null)
        if (categoryId == null)
            categoryId = appPrefs.user?.business?.businessCategory?.masterBusinessCategoryId
        if (categoryId == null) {
            toast("Select category")
            finish()
            return
        }
        presenter.getSubscriptionPlans(categoryId)
    }

    override fun onPlansResponse(list: List<Plan>?) {
        if (list != null && list.isNotEmpty()) {
            plan = list[0]
            binding.plan = plan
            binding.isLoading = false
        } else {
            toast("No plans found")
            finish()
        }
    }

    override fun onTxnCancelled() {
        appPrefs.summary = null
        hashResponse = null
        binding.isLoading = false
    }

    private val onContinueClick = View.OnClickListener {
        if (plan == null)
            toast("Select plan")

        generateHashForPayment(plan!!)
    }

    private fun generateHashForPayment(plan: Plan) {
        val txId = generateTransactionID()
        val requestParams = GenerateHashRequest(
            plan.id,
            appPrefs.user!!.emailId,
            appPrefs.user!!.firstName,
            plan.name,
            txId,
            "",
            "",
            "",
            "",
            ""
        )

        presenter.generateHash(requestParams, plan)
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        toast(error.message)
    }


    override fun onHashGenerated(request: GenerateHashRequest, response: GenerateHashResponse, plan: Plan) {
        initiatePayment(request, response, plan)
    }

    private fun generateTransactionID(): String {
        // val user: User = appPrefs.user ?: throw IllegalStateException("User cannot be null")
        return "${System.currentTimeMillis()}${Random().nextInt(999999) + 100000}"
    }

    override fun showProgress() {
        super.showProgress()
        binding.isLoading = true
    }

    private fun initiatePayment(
        requestParams: GenerateHashRequest,
        response: GenerateHashResponse,
        plan: Plan
    ) {
        val builder: PayUmoneySdkInitializer.PaymentParam.Builder = PayUmoneySdkInitializer.PaymentParam.Builder()
        builder.setAmount(String.format("%.2f", response.txn.totalAmount))
            .setTxnId(requestParams.txnid)
            .setPhone(appPrefs.user!!.phone)
            .setProductName(plan.name)
            .setFirstName(appPrefs.user!!.firstName)
            .setEmail(appPrefs.user!!.emailId)
            .setsUrl("${Env.SERVER_BASE_URL}payment/success")
            .setfUrl("${Env.SERVER_BASE_URL}payment/failure")
            .setUdf1(requestParams.udf1)
            .setUdf2(requestParams.udf2)
            .setUdf3(requestParams.udf3)
            .setUdf4(requestParams.udf4)
            .setUdf5(requestParams.udf5)
            .setKey(Env.MERCHANT_KEY)
            .setMerchantId(Env.MERCHANT_ID)
            .setIsDebug(BuildConfig.DEBUG)
        val paymentParams: PayUmoneySdkInitializer.PaymentParam = builder.build()
        paymentParams.setMerchantHash(response.hash)

        PayUmoneyFlowManager.startPayUMoneyFlow(
            paymentParams,
            this,
            R.style.AppTheme_NoActionBar,
            true
        )
        hashResponse = response
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {

            val transactionResponse: TransactionResponse? =
                data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE)
            val resultModel: ResultModel? = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT)

            Log.d(TAG, "transactionResponse: $transactionResponse")
            Log.d(TAG, "resultModel : $resultModel")

            if (transactionResponse?.getPayuResponse() != null) {
                val payuResponse = transactionResponse.getPayuResponse()
                val payUResponse: PayUResponse? =
                    Gson().fromJson(payuResponse, PayUResponse::class.java)
                payUResponse?.result?.let { payUResult ->
                    val summary = PaymentSummary(
                        hashResponse!!.txn.id,
                        hashResponse!!.txn.txnid,
                        payUResult.mihpayid,
                        payUResult.status,
                        payUResult.error,
                        payUResult.mode,
                        payUResult.bankcode,
                        payUResult.bankRefNum,
                        payUResult.payuMoneyId,
                        payUResult.productinfo,
                        payUResult.firstname,
                        payUResult.email,
                        payUResult.udf1,
                        payUResult.udf2,
                        payUResult.udf3,
                        payUResult.udf4,
                        payUResult.udf5,
                        hashResponse?.txn?.createdAt!!
                    )
                    appPrefs.summary = summary
                    launchActivity<PlanPaymentResultScreen> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra(PlanPaymentResultScreen.PAYMENT_SUMMARY, summary)
                    }
                    finish()
                }
            } else if (resultModel?.error != null) {
                Log.d(TAG, "Error response : " + resultModel.error.transactionResponse)
            } else {
                Log.d(TAG, "Both objects are null!")
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == Activity.RESULT_CANCELED) {
            toast("Payment Cancelled")
            hashResponse?.txn?.id?.let { txnId ->
                presenter.cancelTxn(txnId)
            }
        }

    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
