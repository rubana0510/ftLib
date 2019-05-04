package com.feedbacktower.network.manager

import com.feedbacktower.network.models.EmptyResponse
import com.feedbacktower.network.models.GetCategoriesResponse
import com.feedbacktower.network.models.UpdateProfileResponse
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
        onComplete: (UpdateProfileResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updatePersonalDetailsAsync(
                hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "dob" to dob,
                    "emailId" to email
                )
            ).makeRequest(onComplete)
        }
    }


    fun updateBusinessBasicDetails(
        name: String,
        regNo: String,
        categoryId: String,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessDetailsAsync(
                hashMapOf(
                    "name" to name,
                    "regNo" to regNo,
                    "categoryId" to categoryId
                )
            ).makeRequest(onComplete)
        }
    }

    fun updateBusinessAddressDetails(
        address: String,
        contact: String,
        website: String,
        lat: Double?,
        long: Double?,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessAddressAsync(
                hashMapOf(
                    "address" to address,
                    "phone" to contact,
                    "website" to website,
                    "lat" to lat,
                    "long" to long
                )
            ).makeRequest(onComplete)
        }
    }

    fun getAllCategories(
        onComplete: (GetCategoriesResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getCategoriesAsync().makeRequest(onComplete)
        }
    }

    fun setUnsetCategoryInterest(
        businessCategoryId: String,
        interest: Boolean,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.setBusinessCategoryInterestAsync(
                hashMapOf("businessCategoryId" to businessCategoryId, "interest" to interest)
            ).makeRequest(onComplete)
        }
    }

}