package com.feedbacktower.ui.plans

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.BuildConfig
import com.feedbacktower.BusinessMainActivity
import com.feedbacktower.R
import com.feedbacktower.adapters.PlanAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.ActivitySubscriptionPlanScreenBinding
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.manager.TransactionManager
import com.feedbacktower.network.models.GenerateHashRequest
import com.feedbacktower.network.models.PlanListResponse
import com.feedbacktower.util.launchActivity
import com.payumoney.core.PayUmoneySdkInitializer
import com.payumoney.core.entity.TransactionResponse
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager
import org.jetbrains.anko.toast
import com.payumoney.sdkui.ui.utils.ResultModel


class SubscriptionPlansScreen : AppCompatActivity() {

    private lateinit var planListView: RecyclerView
    private lateinit var planAdapter: PlanAdapter
    private var plan: PlanListResponse.Plan? = null
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
        binding.isLoading = true
        ProfileManager.getInstance()
            .getSubscriptionPlans { planListResponse, error ->
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

    private fun generateHashForPayment(plan: PlanListResponse.Plan) {
        val txId = "TXID${System.currentTimeMillis()}"
        val requestParams: GenerateHashRequest = GenerateHashRequest(
            plan.fee,
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
                    return@generateHash
                }
                initiatePayment(requestParams, response.hash, plan)
            }
    }

    private fun initiatePayment(requestParams: GenerateHashRequest, hash: String, plan: PlanListResponse.Plan) {
        val builder: PayUmoneySdkInitializer.PaymentParam.Builder = PayUmoneySdkInitializer.PaymentParam.Builder()

        builder.setAmount(plan.fee)
            .setTxnId(requestParams.txnid)
            .setPhone(user.phone)
            .setProductName(plan.name)
            .setFirstName(user.firstName)
            .setEmail(user.emailId)
            .setsUrl("feedbacktower.com/success")
            .setfUrl("feedbacktower.com/failure")
            .setUdf1(requestParams.udf1)
            .setUdf2(requestParams.udf2)
            .setUdf3(requestParams.udf3)
            .setUdf4(requestParams.udf4)
            .setUdf5(requestParams.udf5)
            .setIsDebug(true)
            .setKey(BuildConfig.MERCHANT_KEY)
            .setMerchantId(BuildConfig.MERCHANT_ID)

        val paymentParams: PayUmoneySdkInitializer.PaymentParam = builder.build()
        paymentParams.setMerchantHash(hash)

        PayUmoneyFlowManager.startPayUMoneyFlow(paymentParams, this, com.feedbacktower.R.style.AppTheme_NoActionBar, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            val transactionResponse: TransactionResponse? =
                data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE)
            val resultModel: ResultModel? = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT)

            if (transactionResponse?.getPayuResponse() != null) {

                if (transactionResponse.transactionStatus == TransactionResponse.TransactionStatus.SUCCESSFUL) {
                    //Success Transaction
                    toast("Payment Successful")
                    launchActivity<BusinessMainActivity>()
                } else {
                    //Failure Transaction
                    toast("Payment is unsuccessful, Please try again.")
                }
                // Response from Payumoney
                val payuResponse = transactionResponse.getPayuResponse()

                Log.d(TAG, "payuResponse: " + payuResponse)
                // Response from SURl and FURL
                val merchantResponse = transactionResponse.transactionDetails

                Log.d(TAG, "merchantResponse : " + merchantResponse)
            } else if (resultModel?.error != null) {
                Log.d(TAG, "Error response : " + resultModel.error.transactionResponse)
            } else {
                Log.d(TAG, "Both objects are null!")
            }
        }
    }
}
