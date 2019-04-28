package com.feedbacktower.network.utils

import android.util.Log
import kotlinx.coroutines.Deferred
import retrofit2.HttpException

const val TAG = "ApiService"
suspend fun <T> Deferred<T>.makeRequest(onComplete: (T?, Throwable?) -> Unit) {
    try {
        val response = this.await()
        Log.d(TAG, "Success: $response")
        onComplete(response, null)
    } catch (e: HttpException) {
        e.printStackTrace()
        onComplete(null, Throwable("Network error occurred"))
    } catch (e: Throwable) {
        e.printStackTrace()
        onComplete(null, Throwable("Some error occurred"))
    }
}
