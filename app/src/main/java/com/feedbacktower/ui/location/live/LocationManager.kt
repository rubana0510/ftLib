package com.feedbacktower.ui.location.live

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.EmptyResponse
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeRequest
import com.feedbacktower.util.toArray
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LocationManager @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
)  {

    fun saveCurrentLocation(
        location: LatLng,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        val map = hashMapOf<String, Any?>("currentLocation" to location.toArray())
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessAsync(map).makeRequest(onComplete)
        }
    }
}
