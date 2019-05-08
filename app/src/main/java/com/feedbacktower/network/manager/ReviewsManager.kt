package com.feedbacktower.network.manager

import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ReviewsManager {
    private val TAG = "ReviewsManager"
    private val apiService: ApiServiceDescriptor by lazy {
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

    fun getMyReviews(timestamp: String, onComplete: (GetReviewsResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getMyReviewsAsync(timestamp)
                .makeRequest(onComplete)
        }
    }
    fun addReview(payload: HashMap<String, Any?>, onComplete: (EmptyResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.addReviewAsync(payload)
                .makeRequest(onComplete)
        }
    }

}