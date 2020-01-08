package com.feedbacktower.ui.location.update

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.User
import com.feedbacktower.network.manager.LocationManager
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.network.utils.makeRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import com.feedbacktower.util.toArray
import com.feedbacktower.util.toLocation
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class BusinessLocationPresenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<BusinessLocationContract.View>(),
    BusinessLocationContract.Presenter {
    val user: User?
        get() = appPrefs.user

    override fun saveLocation(location: LatLng) {
        val map = hashMapOf<String, Any?>("location" to location.toArray())
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.updateBusinessAsync(map).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            appPrefs.apply {
                user = user?.apply {
                    business?.apply {
                        this.location = location.toLocation()
                    }
                }
            }
            view?.onSaved(location)
        }
    }
}