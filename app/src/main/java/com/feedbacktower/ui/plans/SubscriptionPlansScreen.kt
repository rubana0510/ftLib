package com.feedbacktower.ui.plans

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.BuildConfig
import com.feedbacktower.R
import com.feedbacktower.adapters.PlanAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.PayUResponse
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.Plan
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.ActivitySubscriptionPlanScreenBinding
import com.feedbacktower.network.env.Env
import com.feedbacktower.network.env.Environment
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.TransactionManager
import com.feedbacktower.network.models.GenerateHashRequest
import com.feedbacktower.network.models.GenerateHashResponse
import com.feedbacktower.ui.splash.SplashScreen
import com.feedbacktower.ui.payment.PlanPaymentResultScreen
import com.feedbacktower.util.*
import com.google.gson.Gson
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import com.payumoney.sdkui.ui.utils.ResultModel
import kotlinx.android.synthetic.main.activity_subscription_plan_screen.*
import org.jetbrains.anko.toast
import java.util.*


class SubscriptionPlansScreen : AppCompatActivity() {

    private lateinit var planListView: RecyclerView
    private lateinit var planAdapter: PlanAdapter
    private var hashResponse: GenerateHashResponse? = null
    private var plan: Plan? = null
    private lateinit var user: User
    private lateinit var binding: ActivitySubscriptionPlanScreenBinding
    private val TAG = "SubscriptionPlansScreen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subscription_plan_screen)
        initUi()
        user = AppPrefs.getInstance(this).user!!
    }

    private fun initUi() {
        /* planAdapter = PlanAdapter()
         planListView = binding.planListView
         planListView.setHorizontal(this)
         planListView.adapter = planAdapter*/
        binding.onContinueClick = onContinueClick

        val pendingPaymentSummary = AppPrefs.getInstance(this).summary
        if (pendingPaymentSummary != null)
            saveTransactionStatus(pendingPaymentSummary)
        else
            getPlanList()
    }


    private fun getPlanList() {
        var categoryId = AppPrefs.getInstance(this).getValue("MASTER_CAT_ID")
        if (categoryId == null)
            categoryId = AppPrefs.getInstance(this).user?.business?.businessCategory?.masterBusinessCategoryId
        if (categoryId == null) {
            toast("Select category")
            finish()
            return
        }
        ProfileManager.getInstance()
            .getSubscriptionPlans(categoryId) { planListResponse, error ->
                if (error != null) {
                    toast(error.message ?: getString(R.string.default_err_message))
                    return@getSubscriptionPlans
                }
                if (planListResponse != null && planListResponse.list.isNotEmpty()) {
                    // planAdapter.submitList(planListResponse.list)
                    plan = planListResponse.list[0]
                    binding.plan = plan
                    binding.isLoading = false
                } else {
                    toast("No plans found")
                    finish()
                }
            }
    }

    private fun saveTransactionStatus(summary: PaymentSummary) {
        showLoading()
        binding.isLoading = true
        TransactionManager.getInstance()
            .saveResponse(summary) { _, error ->
                hideLoading()
                if (error == null) {
                    AppPrefs.getInstance(this).summary
                    launchActivity<SplashScreen> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                } else {
                    //toast(error.message ?: getString(R.string.default_err_message))
                    getPlanList()
                    Log.e(TAG, "Could not save status")
                }
            }
    }

    private fun saveTransactionCancelled() {
        val id = hashResponse?.txn?.id
        if (id == null) {
            toast("Could not save transaction status")
            return
        }
        showLoading()
        TransactionManager.getInstance()
            .cancel(id) { _, error ->
                hideLoading()
                if (error == null) {
                    AppPrefs.getInstance(this).summary = null
                    hashResponse = null
                } else {
                    Log.e(TAG, "Could not save status")
                }
            }
    }

    private val onContinueClick = View.OnClickListener {
        if (plan == null)
            toast("Select plan")

        generateHashForPayment(plan!!)
    }

    private fun generateHashForPayment(plan: Plan) {
        showLoading()
        val txId = generateTransactionID()
        val requestParams: GenerateHashRequest = GenerateHashRequest(
            plan.id,
            user.emailId,
            user.firstName,
            plan.name,
            txId,
            "",
            "",
            "",
            "",
            ""
        )
        TransactionManager.getInstance()
            .generateHash(requestParams) { response, error ->
                if (error != null || response == null) {
                    toast(getString(R.string.default_err_message))
                    hideLoading()
                    return@generateHash
                }
                initiatePayment(requestParams, response, plan)
            }
    }

    private fun generateTransactionID(): String {
        // val user: User = AppPrefs.getInstance(this).user ?: throw IllegalStateException("User cannot be null")
        return "${System.currentTimeMillis()}${Random().nextInt(999999) + 100000}"
    }

    private fun showLoading() {
        continueButton.disable()
        continueButton.text = "Please wait..."
        progress.visible()
    }

    private fun hideLoading() {
        continueButton.enable()
        continueButton.text = "CONTINUE"
        progress.gone()
    }

    private fun initiatePayment(
        requestParams: GenerateHashRequest,
        response: GenerateHashResponse,
        plan: Plan
    ) {
        val builder: PayUmoneySdkInitializer.PaymentParam.Builder = PayUmoneySdkInitializer.PaymentParam.Builder()
        builder.setAmount(String.format("%.2f", response.txn.totalAmount))
            .setTxnId(requestParams.txnid)
            .setPhone(user.phone)
            .setProductName(plan.name)
            .setFirstName(user.firstName)
            .setEmail(user.emailId)
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
                    AppPrefs.getInstance(this).summary = summary
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
            saveTransactionCancelled()
        }

    }
}
