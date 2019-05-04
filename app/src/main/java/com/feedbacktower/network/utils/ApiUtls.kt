package com.feedbacktower.network.utils

import android.util.Log
import com.feedbacktower.network.models.ApiResponse
import kotlinx.coroutines.Deferred
import retrofit2.HttpException

const val TAG = "ApiService"
suspend fun <T> Deferred<ApiResponse<T>>.makeRequest(onComplete: (T?, Throwable?) -> Unit) {
    try {
        val response = this.await()
        if (response.error != null)
            onComplete(null, Throwable(response.error.msg))
        else{
            Log.d(TAG, "Payload: ${response.payload}")
            onComplete(response.payload, null)
        }
    } catch (e: HttpException) {
        e.printStackTrace()
        onComplete(null, Throwable("Network error occurred"))
    } catch (e: Throwable) {
        e.printStackTrace()
        onComplete(null, Throwable("Some error occurred"))
    }
}
