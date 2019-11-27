package com.feedbacktower.ui.account.business

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.Business
import com.feedbacktower.data.models.MyBusiness
import com.feedbacktower.data.models.User
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountPresenter
@Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<AccountContract.View>(),
    AccountContract.Presenter {

    val user: User?
        get() = appPrefs.user

    override fun changeAvailability() {
        val isAvailable = appPrefs.user?.business?.available ?: return
        val availability = !isAvailable
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
            appPrefs.apply {
                user = user?.apply {
                    business = business?.apply {
                        available = availability
                    }
                }
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
            appPrefs.apply {
                user = user?.apply {
                    business = response.payload?.business
                }
            }
            getView()?.onFetched(response.payload)
        }
    }

    override fun logOut() {
        appPrefs.clearUserPrefs()
    }
}