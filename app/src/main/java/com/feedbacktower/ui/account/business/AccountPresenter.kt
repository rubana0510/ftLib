package com.feedbacktower.ui.account.business

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountPresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<AccountContract.View>(),
    AccountContract.Presenter {
    override fun changeAvailability(availability: Boolean) {
        GlobalScope.launch(Dispatchers.Main) {
            getView()?.showAvailabilityChangeProgress()
            val response = apiService.updateBusinessAsync(
                hashMapOf("available" to availability)
            ).awaitNetworkRequest()
            getView()?.dismissAvailabilityChangeProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onAvailabilityChanged(availability)
        }
    }

    override fun fetch() {
        GlobalScope.launch(Dispatchers.Main) {
            getView()?.showProgress()
            val response = apiService.getMyBusinessAsync().awaitNetworkRequest()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onFetched(response.payload)
        }
    }
}