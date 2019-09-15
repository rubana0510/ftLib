package com.feedbacktower.ui.reviews

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedbacktower.data.repository.reviews.ReviewsRepository
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetReviewsResponse

class ReviewsViewModel(
    private val repository: ReviewsRepository
) : ViewModel() {
    val getMyReviewsResponse = MutableLiveData<ApiResponse<GetReviewsResponse?>>()
    val getBusinessReviewsResponse = MutableLiveData<ApiResponse<GetReviewsResponse?>>()

    suspend fun addReview(payload: HashMap<String, Any?>) = repository.addReview(payload)

    suspend fun getBusinessReviews(businessId: String, timestamp: String) {
        val response = repository.getBusinessReviews(businessId, timestamp)
        getBusinessReviewsResponse.postValue(response.value)
    }

    suspend fun getMyBusinessReviews(timestamp: String) {
        val response = repository.getMyBusinessReviews(timestamp)
        getMyReviewsResponse.postValue(response.value)
    }
}