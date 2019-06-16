package com.feedbacktower.ui.plans

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.BuildConfig
import com.feedbacktower.R
import com.feedbacktower.adapters.PlanAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Plan
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.ActivitySubscriptionPlanScreenBinding
import com.feedbacktower.network.env.Environment
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.TransactionManager
import com.feedbacktower.network.models.GenerateHashRequest
import com.feedbacktower.network.models.GenerateHashResponse
import com.feedbacktower.payment.models.PayUResponseSuccess
import com.feedbacktower.payment.models.PayUResponseFailure
import com.feedbacktower.ui.LoginScreen
import com.feedbacktower.ui.payment.PlanPaymentSuccessScreen
import com.feedbacktower.util.*
import com.google.gson.Gson
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import org.jetbrains.anko.toast
import com.payumoney.sdkui.ui.utils.ResultModel
import kotlinx.android.synthetic.main.activity_subscription_plan_screen.*


class SubscriptionPlansScreen : AppCompatActivity() {

    private lateinit var planListView: RecyclerView
    private lateinit var planAdapter: PlanAdapter
    private var hashResponse: GenerateHashResponse? = null
    private var plan: Plan? = null
    private lateinit var user: User
    private val TAG = "SubscriptionPlansScreen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySubscriptionPlanScreenBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_subscription_plan_screen)
        initUi(binding)
        user = AppPrefs.getInstance(this).user!!
    }

    private fun initUi(binding: ActivitySubscriptionPlanScreenBinding) {
        /* planAdapter = PlanAdapter()
         planListView = binding.planListView
         planListView.setHorizontal(this)
         planListView.adapter = planAdapter*/
        binding.onContinueClick = onContinueClick

        getPlanList(binding)
    }


    private fun getPlanList(binding: ActivitySubscriptionPlanScreenBinding) {
        var categoryId = AppPrefs.getInstance(this).getValue("MASTER_CAT_ID")
        if (categoryId == null)
            categoryId = AppPrefs.getInstance(this).user?.business?.businessCategory?.masterBusinessCategoryId
        if (categoryId == null) {
            toast("Select category")
            finish()
            return
        }
        binding.isLoading = true
        ProfileManager.getInstance()
            .getSubscriptionPlans(categoryId) { planListResponse, error ->
                binding.isLoading = false
                if (error != null) {
                    toast(error.message ?: getString(R.string.default_err_message))
                    return@getSubscriptionPlans
                }
                if (planListResponse != null && planListResponse.list.isNotEmpty()) {
                    // planAdapter.submitList(planListResponse.list)
                    plan = planListResponse.list[0]
                    binding.plan = plan
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
        val txId = "TXID${System.currentTimeMillis()}"
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
            .setsUrl("${Environment.SERVER_BASE_URL}payment/success")
            .setfUrl("${Environment.SERVER_BASE_URL}payment/failure")
            .setUdf1(requestParams.udf1)
            .setUdf2(requestParams.udf2)
            .setUdf3(requestParams.udf3)
            .setUdf4(requestParams.udf4)
            .setUdf5(requestParams.udf5)
            .setKey(Environment.MERCHANT_KEY)
            .setMerchantId(Environment.MERCHANT_ID)
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
        Log.d(TAG, "onActivityResult: requestCode = $requestCode, resultCode = $resultCode")
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            val transactionResponse: TransactionResponse? =
                data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE)
            val resultModel: ResultModel? = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT)

            Log.d(TAG, "transactionResponse: $transactionResponse")
            Log.d(TAG, "resultModel : $resultModel")
            if (transactionResponse?.getPayuResponse() != null) {

                val payuResponse = transactionResponse.getPayuResponse()
                val merchantResponse = transactionResponse.transactionDetails
                Log.d(TAG, "payuResponse: $payuResponse")
                Log.d(TAG, "merchantResponse : $merchantResponse")

                if (transactionResponse.transactionStatus == TransactionResponse.TransactionStatus.SUCCESSFUL) {
                    //Success Transaction
                    val payUResponseSuccess: PayUResponseSuccess? =
                        Gson().fromJson(payuResponse, PayUResponseSuccess::class.java)
                    toast("Payment Successful: Updating status")
                    saveTransactionStatus(
                        com.feedbacktower.network.models.TransactionResponse(
                            payUResponseSuccess?.result?.mihpayid,
                            "success",
                            hashResponse?.txn?.id!!,
                            hashResponse?.txn?.txnid
                        )
                    )

                } else {
                    //Failure Transaction

                    hashResponse = null
                    val payUResponseFailure: PayUResponseFailure? =
                        Gson().fromJson(payuResponse, PayUResponseFailure::class.java)
                    saveTransactionStatus(
                        com.feedbacktower.network.models.TransactionResponse(
                            null,
                            "failure",
                            hashResponse?.txn?.id!!,
                            hashResponse?.txn?.txnid
                        )
                    )
                    toast("Payment is unsuccessful, Please try again.")
                }

            } else if (resultModel?.error != null) {
                Log.d(TAG, "Error response : " + resultModel.error.transactionResponse)
            } else {
                Log.d(TAG, "Both objects are null!")
            }
        }
    }

    private fun saveTransactionStatus(tXresponse: com.feedbacktower.network.models.TransactionResponse) {
        showLoading()
        TransactionManager.getInstance()
            .saveResponse(
                tXresponse
            ) { response, error ->
                hideLoading()
                if (error == null) {
                    if (tXresponse.status.equals("success")) {
                        //showSuccessDialog()
                        launchActivity<PlanPaymentSuccessScreen> {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putExtra("TX_ID", tXresponse.txnNo)
                        }
                        finish()
                    }
                } else {
                    toast(error.message ?: getString(R.string.default_err_message))
                    Log.e(TAG, "Error: ${error.message}")
                }
            }
    }

    private fun showSuccessDialog() {
        if (isFinishing) return
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Payment Success!")
        builder.setMessage("Your payment for selected plan is successful")
        builder.setPositiveButton("OKAY") { _, _ -> navigateToLogin() }
        builder.setOnDismissListener { navigateToLogin() }
        builder.setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

    private fun navigateToLogin() {
        launchActivity<LoginScreen> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        AppPrefs.getInstance(this).apply {
            authToken = null
            user = null
        }
        toast("Please login again")
    }
}
