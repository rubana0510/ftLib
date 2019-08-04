package com.feedbacktower.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.PlanListResponse
import com.feedbacktower.network.service.ApiService

class PlansDataSourceImpl(private val apiService: ApiService) : PlansDataSource {
    private val _fetchedPlans = MutableLiveData<ApiResponse<PlanListResponse?>>()
    override val fetchedSubscriptionPlans: LiveData<ApiResponse<PlanListResponse?>>
        get() = _fetchedPlans

    override suspend fun fetchSubscriptionPlans() {
        val response = apiService.getSubscriptionPlansAsync("1").await()
        _fetchedPlans.postValue(response)
    }
}