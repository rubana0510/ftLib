package com.feedbacktower.ui.plans

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.data.models.Plan
import com.feedbacktower.network.models.GenerateHashRequest
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionPlansPresenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<SubscriptionPlansContract.View>(),
    SubscriptionPlansContract.Presenter {
    override fun getSubscriptionPlans(categoryId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.getSubscriptionPlansAsync(categoryId).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onPlansResponse(response.payload?.list)
        }
    }

    override fun cancelTxn(id: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.cancelTransactionAsync(hashMapOf("id" to id)).awaitNetworkRequest()
            view?.dismissProgress()

            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onTxnCancelled()
        }
    }

    override fun generateHash(request: GenerateHashRequest, plan: Plan) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.generateHashAsync(request).awaitNetworkRequest()
            view?.dismissProgress()

            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            response.payload?.let { view?.onHashGenerated(request, it, plan) }
        }
    }
}