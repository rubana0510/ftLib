package com.feedbacktower.network.service

import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.data.models.User
import com.feedbacktower.network.models.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiServiceDescriptor {

    //PROFILE
    @POST("/auth/login")
    fun loginAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<AuthResponse?>>

    @POST("/auth/pre-register")
    fun preRegisterAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/auth/request-otp")
    fun requestOtpAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/auth/verify-otp")
    fun verifyOtpRegisterAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<TokenRes?>>

    @POST("/auth/register")
    fun registerPhoneAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<AuthResponse?>>

    @POST("/auth/reset-password")
    fun resetPasswordAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/profile/update")
    fun updatePersonalDetailsAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @Multipart
    @POST("/profile/upload")
    fun uploadProfileAsync(
        @Part file: MultipartBody.Part
    ): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/location/city/")
    fun getCitiesAsync(
        @Query("search") search: String = ""
    ): Deferred<ApiResponse<GetCitiesResponse?>>

    @POST("profile/continue-as-customer")
    fun continueAsCustomerAsync(): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/profile/set-business-interest")
    fun setBusinessCategoryInterestAsync(@Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/business/")
    fun getMyBusinessAsync(): Deferred<ApiResponse<MyBusinessResponse?>>

    @GET
    fun autocompleteAsync(
        @Url url: String
    ): Deferred<ApiResponse<AutoCompleteResponse?>>


    //PAYMENTS
    @POST("/transaction/request")
    fun generateHashAsync(@Body params: GenerateHashRequest): Deferred<ApiResponse<GenerateHashResponse?>>

    @POST("/transaction/response")
    fun saveTransactionResponseAsync(@Body params: TransactionResponse): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/transaction/")
    fun getTransactionsAsync(): Deferred<ApiResponse<PlanTransactionsResponse?>>



    //POST
    @POST("/post/")
    fun createTextPostAsync(@Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @Multipart
    @POST("/post/image")
    fun createPhotoPostAsync(
        @Part file: MultipartBody.Part,
        @Part("text") name: RequestBody
    ): Deferred<ApiResponse<EmptyResponse?>>

    @Multipart
    @POST("/post/video")
    fun createVideoPostAsync(
        @Part file: MultipartBody.Part,
        @Part("text") name: RequestBody
    ): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/post/")
    fun getPostsAsync(
        @Query("timestamp") timestamp: String,
        @Query("type") type: String
    ): Deferred<ApiResponse<GetPostsResponse?>>

    @GET("/post/business/{businessId}/")
    fun getBusinessPostsAsync(
        @Path("businessId") businessId: String?,
        @Query("timestamp") timestamp: String,
        @Query("type") type: String
    ): Deferred<ApiResponse<GetPostsResponse?>>

    @PUT("/post/like/{postId}")
    fun likePostAsync(@Path("postId") postId: String): Deferred<ApiResponse<LikeUnlikeResponse?>>

    @PUT("/post/unlike/{postId}")
    fun unLikePostAsync(@Path("postId") postId: String): Deferred<ApiResponse<EmptyResponse?>>


    //REVIEW
    @POST("/review/")
    fun addReviewAsync(@Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/profile/my-reviews")
    fun getMyReviewsAsync(
        @Query("timestamp") timestamp: String = ""
    ): Deferred<ApiResponse<GetReviewsResponse?>>

    @GET("/review/{businessId}/")
    fun getBusinessReviewsAsync(
        @Path("businessId") businessId: String?,
        @Query("timestamp") timestamp: String = ""
    ): Deferred<ApiResponse<GetReviewsResponse?>>


    //SUGGESTION
    @POST("/suggestion/")
    fun addSuggestionAsync(@Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/profile/my-suggestions/")
    fun getMySuggestionsAsync(
        @Query("timestamp") timestamp: String
    ): Deferred<ApiResponse<GetSuggestionsResponse?>>

    @GET("/suggestion/")
    fun getSuggestionsAsync(
        @Query("timestamp") timestamp: String
    ): Deferred<ApiResponse<GetSuggestionsResponse?>>

    @PUT("/suggestion/reply")
    fun replySuggestionAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>


    @GET("/subscription-plan/")
    fun getSubscriptionPlansAsync(
        @Query("category") categoryId: String
    ): Deferred<ApiResponse<PlanListResponse?>>


    //BUSINESS
    @GET("/business-category/")
    fun getCategoriesAsync(): Deferred<ApiResponse<GetCategoriesResponse?>>

    @GET("/business/list/")
    fun searchBusinessAsync(
        @Query("search") search: String = ""
    ): Deferred<ApiResponse<SearchBusinessResponse?>>

    @POST("/business/register")
    fun updateBusinessAddressAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/business/register")
    fun updateBusinessDetailsAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/business/register")
    fun registerAsBusinessAsync(): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/business/{businessId}")
    fun getBusinessDetailsAsync(@Path("businessId") businessId: String): Deferred<ApiResponse<BusinessDetailsResponse?>>


    //QR Transaction
    @POST("/qr-transfer/generate")
    fun generateQrCodeAsync(): Deferred<ApiResponse<QrPaymentStatus?>>

    @POST("/qr-transfer/scan")
    fun scanQrCodeAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<ScanQrResponse?>>

    @POST("/qr-transfer/sender")
    fun checkQrTransferStatusSenderAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<QrStatusSenderResponse?>>

    @POST("/qr-transfer/receiver")
    fun checkQrTransferStatusReceiverAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<QrStatusRecieverResponse?>>

    @POST("/qr-transfer/payment-request")
    fun requestPaymentAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<QrPaymentStatus?>>

    @POST("/qr-transfer/payment-confirm")
    fun confirmPaymentAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<QrPaymentStatus?>>

}