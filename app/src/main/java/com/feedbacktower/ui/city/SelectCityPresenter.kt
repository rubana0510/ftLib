package com.feedbacktower.ui.city

import com.feedbacktower.data.models.City
import com.feedbacktower.data.models.Place
import com.feedbacktower.network.manager.LocationManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.models.AutoCompleteResponse
import com.feedbacktower.ui.base.BasePresenterImpl

class SelectCityPresenter : BasePresenterImpl<SelectCityContract.View>(),
    SelectCityContract.Presenter {
    override fun saveBusinessCity(city: City) {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .updateBusinessCity(city.id.toString()) { _, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@updateBusinessCity
                }
                getView()?.onBusinessCitySaved(city)
            }
    }

    override fun saveUserCity(city: City) {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .updateCity(city.id.toString()) { _, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@updateCity
                }
                getView()?.onUserCitySaved(city)
            }
    }

    override fun fetchCities(keyword: String) {
        getView()?.showProgress()
        LocationManager.getInstance()
            .getCities(keyword) { response, error ->
                if (error != null) {
                    getView()?.dismissProgress()
                    getView()?.showNetworkError(error)
                    return@getCities
                }
                if (response != null && response.cities.isNotEmpty()) {
                    getView()?.dismissProgress()
                    getView()?.onCitiesFetched(response)
                } else {
                    fetchPlaces(keyword)
                }
            }
    }

    override fun createNewCityState(city: String, state: String) {
        getView()?.showProgress()
        LocationManager.getInstance()
            .addNewCity(city, state) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@addNewCity
                }
                response?.city?.let {
                    getView()?.onCityStateCreated(it)
                }
            }
    }

    override fun getPlaceDetails(placeId: String) {
        getView()?.showProgress()
        LocationManager.getInstance()
            .placeDetails(placeId) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@placeDetails
                }
                response?.let {
                    getView()?.onPlaceDetailsFetched(it.result)
                }
            }
    }

    private fun fetchPlaces(keyword: String) {
        getView()?.showProgress()
        LocationManager.getInstance()
            .autocomplete(keyword) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@autocomplete
                }
                val places = getPlaces(response?.predictions)
                getView()?.onPlacesFetched(places)
            }
    }

    private fun getPlaces(predictions: List<AutoCompleteResponse.Prediction>?): List<Place> {
        val list = ArrayList<Place>()
        predictions?.forEach {
            list.add(Place(it.placeId, it.description))
        }
        return list.toMutableList()
    }

}