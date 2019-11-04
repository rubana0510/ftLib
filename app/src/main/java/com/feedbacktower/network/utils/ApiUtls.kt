package com.feedbacktower.network.utils

import android.util.Log
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.Result
import com.feedbacktower.util.Constants
import kotlinx.coroutines.Deferred
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

const val TAG = "ApiService"
suspend fun <T> Deferred<ApiResponse<T>>.makeRequest(onComplete: (T?, ApiResponse.ErrorModel?) -> Unit) {
    try {
        val response = this.await()
        if (response.error != null)
            onComplete(null, response.error)
        else {
            //Log.d(TAG, "Payload: ${response.payload}")
            onComplete(response.payload, null)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        when (e) {
            is HttpException -> {
                onComplete(
                    null,
                    ApiResponse.ErrorModel(
                        Constants.Service.Error.HTTP_EXCEPTION_ERROR_CODE,
                        "Network error occurred",
                        ApiResponse.ErrorType.HTTP_EXCEPTION
                    )
                )
            }
            is SocketTimeoutException -> {
                onComplete(
                    null,
                    ApiResponse.ErrorModel(
                        Constants.Service.Error.SOCKET_TIMEOUT_EXCEPTION_ERROR_CODE,
                        "Poor internet connection, Check your internet",
                        ApiResponse.ErrorType.TIMEOUT
                    )
                )
            }
            is UnknownHostException -> {
                onComplete(
                    null,
                    ApiResponse.ErrorModel(
                        "0",
                        "Could not reach server, Check your internet",
                        ApiResponse.ErrorType.NO_INTERNET
                    )
                )
            }
            else -> {
                onComplete(
                    null,
                    ApiResponse.ErrorModel(
                        "0",
                        "Unknown error occurred [${e}]",
                        ApiResponse.ErrorType.UNKNOWN
                    )
                )
            }
        }
    }
}

suspend fun <T> Deferred<T>.makeRequestThirdParty(onComplete: (T?, Throwable?) -> Unit) {
    try {
        val response = this.await()
        if (response != null)
            onComplete(null, Throwable("Some error"))
        else {
            onComplete(response, null)
        }
    } catch (e: HttpException) {
        e.printStackTrace()
        onComplete(null, Throwable("Network error occurred"))
    } catch (e: Throwable) {
        e.printStackTrace()
        onComplete(null, Throwable("Some error occurred"))
    }
}
