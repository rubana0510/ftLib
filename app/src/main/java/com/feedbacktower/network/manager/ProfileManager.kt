package com.feedbacktower.network.manager

import com.feedbacktower.data.models.User
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileManager {
    private val TAG = "ProfileManager"
    private val apiService: ApiServiceDescriptor by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instance: ProfileManager? = null

        fun getInstance(): ProfileManager =
            instance ?: synchronized(this) {
                instance ?: ProfileManager().also { instance = it }
            }
    }

    fun updatePersonalDetails(
        firstName: String,
        lastName: String,
        email: String,
        dob: String,
        onComplete: (ApiResponse<Nothing>?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updatePersonalDetailsAsync(
                hashMapOf(
                    "firstName" to firstName,
                    "lastname" to lastName,
                    "dob" to dob,
                    "emailId" to email
                )
            )
                .makeRequest(onComplete)
        }
    }


}