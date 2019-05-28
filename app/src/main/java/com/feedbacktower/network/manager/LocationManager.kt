package com.feedbacktower.network.manager

import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import com.feedbacktower.util.toLatLngArray
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationManager {
    private val TAG = "LocationManager"
    private val apiService: ApiServiceDescriptor by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instance: LocationManager? = null
        @JvmStatic
        fun getInstance(): LocationManager =
            instance ?: synchronized(this) {
                instance ?: LocationManager().also { instance = it }
            }
    }

    fun getCities(
        onComplete: (GetCitiesResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getCitiesAsync().makeRequest(onComplete)
        }
    }

    fun saveBusinessLocation(
        location: LatLng,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ){
        val map = hashMapOf<String, Any?>("location" to arrayOf(location.latitude, location.longitude))
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessDetailsAsync(map).makeRequest(onComplete)
        }
    }
    fun saveCurrentLocation(
        location: LatLng,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ){
        val map = hashMapOf<String, Any?>("currentLocation" to arrayOf(location.latitude, location.longitude))
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessDetailsAsync(map).makeRequest(onComplete)
        }
    }
    fun autocomplete(
        query: String,
        onComplete: (AutoCompleteResponse?, Throwable?) -> Unit
    ){
        val url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=${query}&key=<API_KEY>&sessiontoken=1234567890";
        GlobalScope.launch(Dispatchers.Main) {
            apiService.autocompleteAsync(url).makeRequest(onComplete)
        }
    }
}
