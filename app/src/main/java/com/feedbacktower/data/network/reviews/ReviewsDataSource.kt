package com.feedbacktower.data.network.reviews

import androidx.lifecycle.LiveData
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.EmptyResponse
import com.feedbacktower.network.models.GetReviewsResponse

interface ReviewsDataSource {
    val addReviewResponse: LiveData<ApiResponse<EmptyResponse?>>
    val getBusinessReviewsResponse: LiveData<ApiResponse<GetReviewsResponse?>>
    val getMyReviewsResponse: LiveData<ApiResponse<GetReviewsResponse?>>
    suspend fun addReview(payload: HashMap<String, Any?>)
    suspend fun getBusinessReviews(businessId: String, timestamp: String)
    suspend fun getMyReviews(timestamp: String)
}