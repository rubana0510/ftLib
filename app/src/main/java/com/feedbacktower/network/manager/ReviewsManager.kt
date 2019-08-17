package com.feedbacktower.network.manager

import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.EmptyResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ReviewsManager {
    private val TAG = "ReviewsManager"
    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instace: ReviewsManager? = null

        fun getInstance(): ReviewsManager =
            instace ?: synchronized(this) {
                instace ?: ReviewsManager().also { instace = it }
            }
    }

    fun getBusinessReviews(
        businessId: String,
        timestamp: String,
        onComplete: (GetReviewsResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getBusinessReviewsAsync(
                businessId = businessId,
                timestamp = timestamp
            ).makeRequest(onComplete)
        }

    }

    fun getMyReviews(timestamp: String, onComplete: (GetReviewsResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getMyReviewsAsync(
                timestamp = timestamp
            ).makeRequest(onComplete)
        }
    }

    fun addReview(payload: HashMap<String, Any?>, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.addReviewAsync(payload)
                .makeRequest(onComplete)
        }
    }

}