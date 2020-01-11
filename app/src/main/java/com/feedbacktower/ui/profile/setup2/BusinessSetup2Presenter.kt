package com.feedbacktower.ui.profile.setup2

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.User
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class BusinessSetup2Presenter
@Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<BusinessSetup2Contract.View>(),
    BusinessSetup2Contract.Presenter {

    val user: User
        get() = appPrefs.user ?: throw IllegalStateException("User cannot be null")

    override fun updateDetails(address: String, contact: String, website: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.updateBusinessAsync(
                hashMapOf(
                    "address" to address,
                    "phone" to contact,
                    "website" to website,
                    "lat" to null,
                    "long" to null
                )
            ).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            appPrefs.apply {
                user = user?.apply {
                    business = business?.apply {
                        this.address = address
                        this.phone = contact
                        this.website = website
                    }
                }
            }
            view?.onDetailsUpdated(address, contact, website)
        }
    }
}