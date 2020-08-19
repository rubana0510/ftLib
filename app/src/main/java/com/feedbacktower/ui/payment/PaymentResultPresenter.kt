package com.feedbacktower.ui.payment

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.Plan
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import javax.inject.Inject

class PaymentResultPresenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<PaymentResultContract.View>(), PaymentResultContract.Presenter {
    var statusCallCount = 0
    var plan: Plan? = null
    override fun verifyReferralCode(code: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showVerifyReferralProgress()
            val response = apiService.verifyReferralCodeAsync(hashMapOf("code" to code)).awaitNetworkRequest()
            view?.hideVerifyReferralProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.verifyReferralCodeSuccess()
        }
    }

    override fun refreshAuthToken() {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.refreshTokenAsync().awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            response.payload?.let { data ->
                appPrefs.apply {
                    user = data.user
                    authToken = data.token
                }
            }
            view?.refreshAuthTokenSuccess()
        }
    }

    override fun checkPaymentStatus(transactionId: String) {
        statusCallCount++
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.checkPaymentStatusAsync(transactionId).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            response.payload?.transaction?.let {
                view?.checkPaymentStatusSuccess(it)
            }
        }
    }

    override fun saveTransactionStatus(summary: PaymentSummary) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.saveTransactionResponseAsync(summary).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.saveTransactionStatusSuccess()
        }
    }

    override fun getSubscriptionPlan() {
        val categoryId: String = appPrefs.getValue("MASTER_CAT_ID", null)
            ?: throw IllegalStateException("CategoryId cannot be null")

        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.getSubscriptionPlansAsync(categoryId).awaitNetworkRequest()
            if (response.error != null) {
                return@launch
            }
            response.payload?.list?.let{
                if(it.isNotEmpty()){
                    plan = it.first()
                }
            }
        }
    }
}