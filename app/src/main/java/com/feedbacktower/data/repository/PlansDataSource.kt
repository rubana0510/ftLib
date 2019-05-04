package com.feedbacktower.data.repository

import androidx.lifecycle.LiveData
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.network.models.ApiResponse

interface PlansDataSource {
    val fetchedSubscriptionPlans: LiveData<ApiResponse<List<SubscriptionPlan>>>
    suspend fun fetchSubscriptionPlans()
}