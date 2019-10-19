package com.feedbacktower.ui.location.update

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView
import com.google.android.gms.maps.model.LatLng

interface BusinessLocationContract {
    interface View : BaseView {
        fun onSaved(savedLocation: LatLng)
    }

    interface Presenter : BasePresenter<View> {
        fun saveLocation(location: LatLng)
    }
}