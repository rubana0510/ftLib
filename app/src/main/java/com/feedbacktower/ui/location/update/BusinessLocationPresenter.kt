package com.feedbacktower.ui.location.update

import com.feedbacktower.data.models.Location
import com.feedbacktower.network.manager.LocationManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl
import com.google.android.gms.maps.model.LatLng

class BusinessLocationPresenter : BasePresenterImpl<BusinessLocationContract.View>(),
    BusinessLocationContract.Presenter {
    override fun saveLocation(location: LatLng) {
        getView()?.showProgress()
        LocationManager.getInstance()
            .saveBusinessLocation(location) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@saveBusinessLocation
                }
                getView()?.onSaved(location)
            }
    }
}