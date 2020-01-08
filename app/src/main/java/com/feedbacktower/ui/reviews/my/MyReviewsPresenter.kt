package com.feedbacktower.ui.reviews.my

import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyReviewsPresenter@Inject constructor(
    val apiService: ApiService
) : BasePresenterImpl<MyReviewsContract.View>(),
    MyReviewsContract.Presenter {
    override fun fetchReviews(timestamp: String, initial: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response: ApiResponse<GetReviewsResponse?> = apiService.getMyReviewsAsync(timestamp = timestamp).await()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onReviewsFetched(response.payload, initial)
        }
    }
}