package com.feedbacktower.data.repository

import androidx.lifecycle.LiveData
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.PlanListResponse

interface PlansDataSource {
    val fetchedSubscriptionPlans: LiveData<ApiResponse<PlanListResponse?>>
    suspend fun fetchSubscriptionPlans()
}