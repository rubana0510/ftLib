package com.feedbacktower.network.manager

import com.feedbacktower.BuildConfig
import com.feedbacktower.network.models.PlaceDetailsResponse
import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeRequest
import com.feedbacktower.util.Constants
import com.feedbacktower.util.toArray
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class LocationManager {
    private val TAG = "LocationManager"
    private val apiService: ApiService by lazy {
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

    fun saveBusinessLocation(
        location: LatLng,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        val map = hashMapOf<String, Any?>("location" to location.toArray())
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessAsync(map).makeRequest(onComplete)
        }
    }

    fun saveCurrentLocation(
        location: LatLng,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        val map = hashMapOf<String, Any?>("currentLocation" to location.toArray())
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessAsync(map).makeRequest(onComplete)
        }
    }

    fun addNewCity(
        city: String,
        state: String,
        onComplete: (AddCityResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        val map = hashMapOf<String, Any?>("cityName" to city, "stateName" to state)
        GlobalScope.launch(Dispatchers.Main) {
            apiService.addCity(map).makeRequest(onComplete)
        }
    }

    fun getCities(
        keyword: String,
        onComplete: (GetCitiesResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getCitiesAsync(keyword).makeRequest(onComplete)
        }
    }

    fun autocomplete(
        query: String,
        onComplete: (AutoCompleteResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        val url =
            "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=${query}&key=${BuildConfig.PLACES_API_KEY}&sessiontoken=1234567890";
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.autocompleteAsync(url).await()
                if (response.status == "OK")
                    onComplete(response, null)
                else {
                    onComplete(null, ApiResponse.ErrorModel(response.status, "Network error occurred"))
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                onComplete(
                    null,
                    ApiResponse.ErrorModel(Constants.Service.Error.HTTP_EXCEPTION_ERROR_CODE, "Network error occurred")
                )
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
                onComplete(
                    null,
                    ApiResponse.ErrorModel(
                        Constants.Service.Error.SOCKET_TIMEOUT_EXCEPTION_ERROR_CODE,
                        "Timeout error, please try again"
                    )
                )
            }
        }
    }

    fun placeDetails(
        placeId: String,
        onComplete: (PlaceDetailsResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        val url =
            "https://maps.googleapis.com/maps/api/place/details/json?place_id=$placeId&key=${BuildConfig.PLACES_API_KEY}"
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.placeDetailsAsync(url).await()
                if (response.status == "OK")
                    onComplete(response, null)
                else {
                    onComplete(null, ApiResponse.ErrorModel(response.status, "Network error occurred"))
                }
            } catch (e: HttpException) {
                e.printStackTrace()
                onComplete(
                    null,
                    ApiResponse.ErrorModel(
                        Constants.Service.Error.HTTP_EXCEPTION_ERROR_CODE,
                        "Network error occurred"
                    )
                )
            } catch (e: SocketTimeoutException) {
                e.printStackTrace()
                onComplete(
                    null,
                    ApiResponse.ErrorModel(
                        Constants.Service.Error.SOCKET_TIMEOUT_EXCEPTION_ERROR_CODE,
                        "Timeout error, please try again"
                    )
                )
            }
        }
    }
}
