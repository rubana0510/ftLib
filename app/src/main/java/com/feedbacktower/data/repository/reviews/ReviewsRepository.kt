package com.feedbacktower.data.repository.reviews

import androidx.lifecycle.LiveData
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.EmptyResponse
import com.feedbacktower.network.models.GetReviewsResponse

interface ReviewsRepository {
    suspend fun addReview(payload: HashMap<String, Any?>): LiveData<ApiResponse<EmptyResponse?>>
    suspend fun getBusinessReviews(businessId: String, timestamp: String): LiveData<ApiResponse<GetReviewsResponse?>>
    suspend fun getMyBusinessReviews(timestamp: String): LiveData<ApiResponse<GetReviewsResponse?>>
}