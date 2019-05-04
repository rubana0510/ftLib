package com.feedbacktower.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.service.ApiServiceDescriptor

class PlansDataSourceImpl(private val apiService: ApiServiceDescriptor) : PlansDataSource {
    private val _fetchedPlans = MutableLiveData<ApiResponse<List<SubscriptionPlan>>>()
    override val fetchedSubscriptionPlans: LiveData<ApiResponse<List<SubscriptionPlan>>>
        get() = _fetchedPlans

    override suspend fun fetchSubscriptionPlans() {
        val response = apiService.getAllSubscriptionPlans().await()
        _fetchedPlans.postValue(response)
    }
}