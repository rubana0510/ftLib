package com.feedbacktower.ui.reviews.send

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RateReviewPresenter @Inject constructor(
    private val apiService: ApiService
) {
    private var view: RateReviewView? = null
    fun attachView(view: RateReviewView) {
        this.view = view
    }

    fun destroView() {
        view = null
    }

    fun rate(businessId: String, rating: Int, comment: String) {
        val map = hashMapOf<String, Any?>(
            "businessId" to businessId,
            "rating" to rating,
            "comment" to comment
        )
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.addReviewAsync(map).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onRateSuccess()
        }
    }

}