package com.feedbacktower.network.manager

import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationManager {
    private val TAG = "LocationManager"
    private val apiService: ApiServiceDescriptor by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instance: LocationManager? = null

        fun getInstance(): LocationManager =
            instance ?: synchronized(this) {
                instance ?: LocationManager().also { instance = it }
            }
    }

    fun getCities(
        onComplete: (GetCitiesResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getCitiesAsync().makeRequest(onComplete)
        }
    }
}