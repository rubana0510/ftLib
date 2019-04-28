package com.feedbacktower.data

import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.data.repos.BaseRepository
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor

class BusinessCategoriesRepository private constructor() :
    BaseRepository() {

    private val apiService: ApiServiceDescriptor by lazy { ApiService.create() }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: BusinessCategoriesRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: BusinessCategoriesRepository().also { instance = it }
            }
    }

    suspend fun getBusinessCategoriesAsync(): MutableList<BusinessCategory>? {
        val categoriesResponse = safeApiCall(
            call = { apiService.getBusinessCategoriesAsync().await() },
            errorMessage = "Error Fetching Popular Movies"
        )
        return categoriesResponse?.toMutableList()
    }
}