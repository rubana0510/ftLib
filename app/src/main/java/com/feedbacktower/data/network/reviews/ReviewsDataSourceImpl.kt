package com.feedbacktower.data.network.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.EmptyResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.util.toErrorResponse

class ReviewsDataSourceImpl(
    private val apiService: ApiService
) : ReviewsDataSource {

    private val _addReviewResponse = MutableLiveData<ApiResponse<EmptyResponse?>>()
    private val _getBusinessReviewsResponse = MutableLiveData<ApiResponse<GetReviewsResponse?>>()
    private val _getMyReviewsResponse = MutableLiveData<ApiResponse<GetReviewsResponse?>>()

    override val addReviewResponse: LiveData<ApiResponse<EmptyResponse?>>
        get() = _addReviewResponse
    override val getBusinessReviewsResponse: LiveData<ApiResponse<GetReviewsResponse?>>
        get() = _getBusinessReviewsResponse
    override val getMyReviewsResponse: LiveData<ApiResponse<GetReviewsResponse?>>
        get() = _getMyReviewsResponse

    override suspend fun addReview(payload: HashMap<String, Any?>) {
        try {
            val response = apiService.addReviewAsync(payload).await()
            _addReviewResponse.postValue(response)
        } catch (e: Exception) {
            _addReviewResponse.postValue(e.toErrorResponse())
        }
    }

    override suspend fun getBusinessReviews(businessId: String, timestamp: String) {
        try {
            val response = apiService.getBusinessReviewsAsync(businessId, timestamp).await()
            _getBusinessReviewsResponse.postValue(response)
        } catch (e: Exception) {
            _getBusinessReviewsResponse.postValue(e.toErrorResponse())
        }
    }

    override suspend fun getMyReviews(timestamp: String) {
        try {
            val response = apiService.getMyReviewsAsync(timestamp).await()
            _getMyReviewsResponse.postValue(response)
        } catch (e: Exception) {
            _getMyReviewsResponse.postValue(e.toErrorResponse())
        }
    }

}
