package com.feedbacktower.network.manager

import android.util.Log
import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import com.feedbacktower.util.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

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
        onComplete: (EmptyResponse?, Throwable?) -> Unit
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
        cityId: String,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessDetailsAsync(
                hashMapOf(
                    "name" to name,
                    "regNo" to regNo,
                    "businessCategoryId" to categoryId,
                    "cityId" to cityId
                )
            ).makeRequest(onComplete)
        }
    }

    fun updateCity(
        cityId: String,
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updatePersonalDetailsAsync(
                hashMapOf(
                    "cityId" to cityId
                )
            ).makeRequest(onComplete)
        }
    }

    fun continueAsCustomer(
        onComplete: (EmptyResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.continueAsCustomerAsync().makeRequest(onComplete)
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

    fun getSubscriptionPlans(
        categoryId: String,
        onComplete: (PlanListResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getSubscriptionPlansAsync(categoryId).makeRequest(onComplete)
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

    fun getBusinessDetails(
        businessId: String,
        onComplete: (BusinessDetailsResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getBusinessDetailsAsync(
                businessId
            ).makeRequest(onComplete)
        }
    }

    fun searchBusiness(
        search: String,
        onComplete: (SearchBusinessResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.searchBusinessAsync(
                search
            ).makeRequest(onComplete)
        }
    }

    fun uploadProfile(file: File, onComplete: (EmptyResponse?, Throwable?) -> Unit) {
        if (!file.exists()) {
            Log.e(TAG, "uploadImages: FileNotFound")
            return
        }
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val filePart = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
        GlobalScope.launch(Dispatchers.Main) {
            apiService.uploadProfileAsync(
                filePart
            ).makeRequest(onComplete)
        }
    }

    fun getMyBusiness(onComplete: (MyBusinessResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getMyBusinessAsync().makeRequest(onComplete)
        }
    }

}