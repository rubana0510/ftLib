package com.feedbacktower.data.repository.reviews

import androidx.lifecycle.LiveData
import com.feedbacktower.data.network.reviews.ReviewsDataSource
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.EmptyResponse
import com.feedbacktower.network.models.GetReviewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewsRepositoryImpl(
    private val dataSource: ReviewsDataSource
) : ReviewsRepository {
    override suspend fun addReview(payload: HashMap<String, Any?>): LiveData<ApiResponse<EmptyResponse?>> {
        return withContext(Dispatchers.IO) {
            dataSource.addReview(payload)
            dataSource.addReviewResponse
        }
    }

    override suspend fun getBusinessReviews(
        businessId: String,
        timestamp: String
    ): LiveData<ApiResponse<GetReviewsResponse?>> {
        return withContext(Dispatchers.IO) {
            dataSource.getBusinessReviews(businessId, timestamp)
            dataSource.getBusinessReviewsResponse
        }
    }

    override suspend fun getMyBusinessReviews(timestamp: String): LiveData<ApiResponse<GetReviewsResponse?>> {
        return withContext(Dispatchers.IO) {
            dataSource.getMyReviews(timestamp)
            dataSource.getMyReviewsResponse
        }
    }
}