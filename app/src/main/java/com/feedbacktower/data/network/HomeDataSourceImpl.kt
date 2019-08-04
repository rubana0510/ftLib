package com.feedbacktower.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feedbacktower.data.models.Ad
import com.feedbacktower.exception.NoConnectivityException
import com.feedbacktower.network.service.ApiService
import retrofit2.HttpException
import java.net.SocketTimeoutException

class HomeDataSourceImpl(
    private val apiService: ApiService
) : HomeDataSource {
    private val _banners = MutableLiveData<List<Ad>>()
    override val ads: LiveData<List<Ad>>
        get() = _banners

    override suspend fun fetchAds() {
        try {
            val response = apiService.getAdsAsync().await()
            _banners.postValue(response.payload?.ads)
        } catch (e: NoConnectivityException) {
            Log.e("fetchServices", "Network error occurred", e)
        } catch (e: HttpException) {
            Log.e("SubcategoryData", "HttpException", e)
        } catch (e: SocketTimeoutException) {
            Log.e("SubcategoryData", "SocketTimeoutException", e)
        }
    }
}