package com.feedbacktower.ui.plans

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.data.repository.PlansDataSource
import com.feedbacktower.data.repository.PlansDataSourceImpl
import com.feedbacktower.network.models.ApiResponse
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SubscriptionPlanViewModel internal constructor(private val plansDataSourceImpl: PlansDataSourceImpl) :
    ViewModel() {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)

    val plans = plansDataSourceImpl.fetchedSubscriptionPlans

    fun fetchBusinessCategories() {
        scope.launch {
            plansDataSourceImpl.fetchSubscriptionPlans()
        }
    }


    fun cancelAllRequests() = coroutineContext.cancel()
}