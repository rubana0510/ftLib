package com.feedbacktower.ui.city

import com.feedbacktower.data.models.City
import com.feedbacktower.data.models.Place
import com.feedbacktower.network.models.GetCitiesResponse
import com.feedbacktower.network.models.PlaceDetailsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface SelectCityContract {
    interface View : BaseView {
        fun onCitiesFetched(response: GetCitiesResponse?)
        fun onPlacesFetched(places: List<Place>?)
        fun onPlaceDetailsFetched(placeDetails: PlaceDetailsResponse.Result)
        fun onCityStateCreated(city: City)
        fun onUserCitySaved(city: City)
        fun onBusinessCitySaved(city: City)
    }

    interface Presenter : BasePresenter<View> {
        fun fetchCities(keyword: String)
        fun getPlaceDetails(placeId: String)
        fun createNewCityState(city: String, state: String)
        fun saveUserCity(city: City)
        fun saveBusinessCity(city: City)
    }
}