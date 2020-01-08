package com.feedbacktower.ui.business_detail

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.User
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class BusinessDetailPresenter
@Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<BusinessDetailContract.View>(),
    BusinessDetailContract.Presenter {

    val user: User?
        get() = appPrefs.user


    override fun fetchPosts(businessId: String, timestamp: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            //view?.showProgress()
            val response = apiService.getBusinessPostsAsync(businessId, timestamp).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onPostsFetched(response.payload)
        }
    }

    override fun fetchReviews(businessId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.getBusinessReviewsAsync(businessId).awaitNetworkRequest()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onReviewsFetched(response.payload)
        }
    }

    override fun fetchBusinessDetails(businessId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.getBusinessDetailsAsync(businessId).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onBusinessDetailFetched(response.payload)
        }
    }



}