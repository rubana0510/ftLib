package com.feedbacktower.ui.city

import com.feedbacktower.BuildConfig
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.City
import com.feedbacktower.data.models.Place
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.AutoCompleteResponse
import com.feedbacktower.network.models.PlaceDetailsResponse
import com.feedbacktower.network.models.getUnknownError
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitGoogleApiRequest
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SelectCityPresenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<SelectCityContract.View>(),
    SelectCityContract.Presenter {
    override fun saveBusinessCity(city: City) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.updateBusinessAsync(
                hashMapOf(
                    "cityId" to city.id
                )
            ).awaitNetworkRequest()
            view?.dismissProgress()

            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            appPrefs.apply {
                user = user?.apply {
                    this.business = business?.apply {
                        this.city = city
                    }
                }
            }
            view?.onBusinessCitySaved(city)
        }
    }

    override fun saveUserCity(city: City) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.updatePersonalDetailsAsync(
                hashMapOf(
                    "cityId" to city.id
                )
            ).awaitNetworkRequest()
            view?.dismissProgress()

            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            appPrefs.apply {
                setValue("USER_CITY", city.id.toString())
                setValue("CITY", city.name)
                user = user?.apply {
                    this.city = city
                }
            }
            view?.onUserCitySaved(city)
        }
    }

    override fun fetchCities(keyword: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.getCitiesAsync(keyword).awaitNetworkRequest()
            view?.dismissProgress()

            if (response.error != null) {
                view?.dismissProgress()
                view?.showNetworkError(response.error)
                return@launch
            }
            if (response.payload != null && !response.payload.cities.isNullOrEmpty()) {
                view?.dismissProgress()
                view?.onCitiesFetched(response.payload)
            } else {
                fetchPlaces(keyword)
            }
        }
    }

    override fun createNewCityState(city: String, state: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val map = hashMapOf<String, Any?>("cityName" to city, "stateName" to state)
            view?.showProgress()
            val response = apiService.addCity(
                map
            ).awaitNetworkRequest()
            view?.dismissProgress()

            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            response.payload?.city?.let {
                view?.onCityStateCreated(it)
            }
        }
    }

    override fun getPlaceDetails(placeId: String) {
        val url =
            "https://maps.googleapis.com/maps/api/place/details/json?place_id=$placeId&key=${BuildConfig.PLACES_API_KEY}"
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response: ApiResponse<PlaceDetailsResponse> = apiService.placeDetailsAsync(url).awaitGoogleApiRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            if (response.payload != null && response.payload.status == "OK") {
                response.payload.result.let {
                    view?.onPlaceDetailsFetched(it)
                }
            } else {
                view?.showNetworkError(getUnknownError(response.payload?.status))
            }
        }
    }

    private fun fetchPlaces(keyword: String) {
        val url =
            "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=${keyword}&key=${BuildConfig.PLACES_API_KEY}&sessiontoken=1234567890";
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.autocompleteAsync(url).awaitGoogleApiRequest()
            view?.dismissProgress()
            if (response.payload != null && response.payload.status == "OK") {
                response.payload.predictions.let {
                    val places = getPlaces(it)
                    view?.onPlacesFetched(places)
                }
            } else {
                view?.showNetworkError(getUnknownError(response.payload?.status))
            }
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