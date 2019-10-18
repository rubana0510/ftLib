package com.feedbacktower.network.manager

import android.util.Log
import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileManager {
    private val TAG = "ProfileManager"
    private val apiService: ApiService by lazy {
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

    fun find(
        qrData: String,
        onComplete: (FindCustomerResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.findCustomerAsync(qrData).makeRequest(onComplete)
        }
    }


    fun updatePersonalDetails(
        firstName: String,
        lastName: String,
        email: String,
        dob: String,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
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

    fun updateFcmToken(
        token: String,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updatePersonalDetailsAsync(
                hashMapOf(
                    "fcmToken" to token
                )
            ).makeRequest(onComplete)
        }
    }


    fun updateBusinessBasicDetails(
        name: String,
        regNo: String,
        categoryId: String,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessAsync(
                hashMapOf(
                    "name" to name,
                    "regNo" to regNo,
                    "businessCategoryId" to categoryId
                )
            ).makeRequest(onComplete)
        }
    }

    fun updateCity(
        cityId: String,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updatePersonalDetailsAsync(
                hashMapOf(
                    "cityId" to cityId
                )
            ).makeRequest(onComplete)
        }
    }

    fun updateBusinessCity(
        cityId: String,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessAsync(
                hashMapOf(
                    "cityId" to cityId
                )
            ).makeRequest(onComplete)
        }
    }

    fun continueAsCustomer(
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
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
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessAsync(
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
        keyword: String = "",
        onComplete: (GetCategoriesResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getCategoriesAsync(
                keyword
            ).makeRequest(onComplete)
        }
    }

    fun getFeaturedCategories(
        keyword: String? = null,
        onComplete: (GetCategoriesResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getFeaturedCategoriesAsync(keyword).makeRequest(onComplete)
        }
    }

    fun getSubscriptionPlans(
        categoryId: String,
        onComplete: (PlanListResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getSubscriptionPlansAsync(categoryId).makeRequest(onComplete)
        }
    }

    fun setUnsetCategoryInterest(
        businessCategoryId: String,
        interest: Boolean,
        onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.setBusinessCategoryInterestAsync(
                hashMapOf("businessCategoryId" to businessCategoryId, "interest" to interest)
            ).makeRequest(onComplete)
        }
    }

    fun getBusinessDetails(
        businessId: String,
        onComplete: (BusinessDetailsResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getBusinessDetailsAsync(
                businessId
            ).makeRequest(onComplete)
        }
    }

    fun searchBusiness(
        search: String?,
        onComplete: (SearchBusinessResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.searchBusinessAsync(
                search
            ).makeRequest(onComplete)
        }
    }

    fun uploadProfile(file: File, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {
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

    fun getMyBusiness(onComplete: (MyBusinessResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getMyBusinessAsync().makeRequest(onComplete)
        }
    }

    fun changeBusinessAvailability(available: Boolean, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.updateBusinessAsync(
                hashMapOf("available" to available)
            ).makeRequest(onComplete)
        }
    }

    fun applyReferralCode(code: String, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.applyReferralCodeAsync(
                hashMapOf("code" to code)
            ).makeRequest(onComplete)
        }
    }

}