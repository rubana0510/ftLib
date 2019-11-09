package com.feedbacktower.ui.reviews.my

import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MyReviewsPresenter : BasePresenterImpl<MyReviewsContract.View>(),
    MyReviewsContract.Presenter {
    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    override fun fetchReviews(timestamp: String, initial: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            getView()?.showProgress()
            val response: ApiResponse<GetReviewsResponse?> = apiService.getMyReviewsAsync(timestamp = timestamp).await()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onReviewsFetched(response.payload, initial)
        }
    }
}