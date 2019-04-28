package com.feedbacktower.network.service

import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.data.models.User
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.TokenRes
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiServiceDescriptor {

    @POST("/auth/login")
    fun loginAsync(
        @Body map: HashMap<String, Any>
    ): Deferred<ApiResponse<User>>

    @POST("/auth/pre-register")
    fun preRegisterAsync(
        @Body map: HashMap<String, Any>
    ): Deferred<ApiResponse<Nothing>>

    @POST("/auth/verify-otp")
    fun verifyOtpRegisterAsync(
        @Body map: HashMap<String, Any>
    ): Deferred<ApiResponse<TokenRes>>


    @POST("/auth/register")
    fun registerPhoneAsync(
        @Body map: HashMap<String, Any>
    ): Deferred<ApiResponse<User>>

    @POST("/profile/update")
    fun updatePersonalDetailsAsync(
        @Body map: HashMap<String, Any>
    ): Deferred<ApiResponse<Nothing>>

    @POST("/projectx/index.php/API/Services/get_auth_pager")
    @FormUrlEncoded
    fun getBusinessCategoriesAsync(
    ): Deferred<Response<List<BusinessCategory>>>

}