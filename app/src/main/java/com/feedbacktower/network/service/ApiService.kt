package com.feedbacktower.network.service

import com.feedbacktower.App
import com.feedbacktower.BuildConfig
import com.feedbacktower.data.models.PaymentSummary
import com.feedbacktower.network.env.Environment
import com.feedbacktower.network.models.*
import com.feedbacktower.util.Constants
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

/**
 * Created by sanket on 25-09-2018.
 */
interface ApiService {

    companion object Factory {
        fun create(): ApiService {
            val clientBuilder = OkHttpClient.Builder().apply {
                connectTimeout(Constants.Service.Timeout.CONNECT, TimeUnit.MILLISECONDS)
                readTimeout(Constants.Service.Timeout.READ, TimeUnit.MILLISECONDS)
                writeTimeout(Constants.Service.Timeout.WRITE, TimeUnit.MILLISECONDS)
                addInterceptor(HttpLoggingInterceptor().apply w@{
                    if (!BuildConfig.DEBUG) return@w
                    //level = HttpLoggingInterceptor.Level.BODY
                    // level = HttpLoggingInterceptor.Level.HEADERS
                })
                addNetworkInterceptor ani@{ chain ->
                    val request = chain.request().newBuilder()
                        .addHeader(Constants.AUTHORIZATION, "Bearer ${App.getInstance().getToken()}")
                        .addHeader("Platform", "android")
                        .addHeader("VersionCode", BuildConfig.VERSION_CODE.toString())
                        .addHeader("VersionName", BuildConfig.VERSION_NAME)
                        .build()
                    return@ani chain.proceed(request);
                }
            }
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(Environment.SERVER_BASE_URL)
                .client(clientBuilder.build())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }

    //PROFILE
    @POST("/api/auth/login")
    fun loginAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<AuthResponse?>>

    @POST("/api/auth/pre-register")
    fun preRegisterAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/api/auth/request-otp")
    fun requestOtpAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/api/auth/verify-otp")
    fun verifyOtpRegisterAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<TokenRes?>>

    @POST("/api/auth/register")
    fun registerPhoneAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<AuthResponse?>>


    @POST("/api/auth/reset-password")
    fun resetPasswordAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/api/auth/")
    fun refreshTokenAsync(): Deferred<ApiResponse<AuthResponse?>>

    //profile
    @POST("/api/profile/update")
    fun updatePersonalDetailsAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @Multipart
    @POST("/api/profile/upload")
    fun uploadProfileAsync(
        @Part file: MultipartBody.Part
    ): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/api/location/city/")
    fun getCitiesAsync(
        @Query("search") search: String = ""
    ): Deferred<ApiResponse<GetCitiesResponse?>>

    @POST("/api/location/city/")
    fun addCity(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<AddCityResponse?>>

    @POST("/api/profile/continue-as-customer")
    fun continueAsCustomerAsync(): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/api/profile/set-business-interest")
    fun setBusinessCategoryInterestAsync(@Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/api/business/")
    fun getMyBusinessAsync(): Deferred<ApiResponse<MyBusinessResponse?>>

    @GET("/api/profile/{qrData}")
    fun findCustomerAsync(
        @Path("qrData") qrData: String
    ): Deferred<ApiResponse<FindCustomerResponse?>>

    @GET
    fun autocompleteAsync(
        @Url url: String
    ): Deferred<AutoCompleteResponse>

    @GET
    fun placeDetailsAsync(
        @Url url: String
    ): Deferred<PlaceDetailsResponse>

    //PAYMENTS
    @POST("/api/transaction/request")
    fun generateHashAsync(@Body params: GenerateHashRequest): Deferred<ApiResponse<GenerateHashResponse?>>

    @POST("/api/transaction/response")
    fun saveTransactionResponseAsync(@Body summary: PaymentSummary): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/api/transaction/cancel")
    fun cancelTransactionAsync(@Body body: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/api/transaction/")
    fun getTransactionsAsync(): Deferred<ApiResponse<PlanTransactionsResponse?>>


    @GET("/api/transaction/{transactionId}")
    fun checkPaymentStatusAsync(
        @Path("transactionId") transactionId: String
    ): Deferred<ApiResponse<PaymentTxnResponse?>>


    //POST
    @POST("/api/post/")
    fun createTextPostAsync(@Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @Multipart
    @POST("/api/post/image")
    fun createPhotoPostAsync(
        @Part file: MultipartBody.Part,
        @Part("text") name: RequestBody
    ): Deferred<ApiResponse<EmptyResponse?>>

    @Multipart
    @POST("/api/post/video")
    fun createVideoPostAsync(
        @Part file: MultipartBody.Part,
        @Part("text") name: RequestBody
    ): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/api/post/")
    fun getPostsAsync(
        @Query("timestamp") timestamp: String?
    ): Deferred<ApiResponse<GetPostsResponse?>>

    @GET("/api/post/business/{businessId}/")
    fun getBusinessPostsAsync(
        @Path("businessId") businessId: String?,
        @Query("timestamp") timestamp: String?
    ): Deferred<ApiResponse<GetPostsResponse?>>

    @PUT("/api/post/like/{postId}")
    fun likePostAsync(@Path("postId") postId: String): Deferred<ApiResponse<LikeUnlikeResponse?>>

    @PUT("/api/post/unlike/{postId}")
    fun unLikePostAsync(@Path("postId") postId: String): Deferred<ApiResponse<EmptyResponse?>>

    @DELETE("/api/post/{postId}")
    fun deletePost(@Path("postId") postId: String): Deferred<ApiResponse<EmptyResponse?>>

    @PUT("/api/post/{postId}")
    fun editTextPostAsync(@Path("postId") postId: String, @Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>


    //REVIEW
    @POST("/api/review/")
    fun addReviewAsync(@Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/api/profile/my-reviews")
    fun getMyReviewsAsync(
        @Query("timestamp") timestamp: String = ""
    ): Deferred<ApiResponse<GetReviewsResponse?>>

    @GET("/api/review/{businessId}/")
    fun getBusinessReviewsAsync(
        @Path("businessId") businessId: String?,
        @Query("timestamp") timestamp: String = ""
    ): Deferred<ApiResponse<GetReviewsResponse?>>


    //SUGGESTION
    @POST("/api/suggestion/")
    fun addSuggestionAsync(@Body map: HashMap<String, Any?>): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/api/profile/my-suggestions/")
    fun getMySuggestionsAsync(
        @Query("timestamp") timestamp: String
    ): Deferred<ApiResponse<GetSuggestionsResponse?>>

    @GET("/api/suggestion/")
    fun getSuggestionsAsync(
        @Query("timestamp") timestamp: String
    ): Deferred<ApiResponse<GetSuggestionsResponse?>>

    @PUT("/api/suggestion/reply")
    fun replySuggestionAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>


    @GET("/api/subscription-plan/")
    fun getSubscriptionPlansAsync(
        @Query("category") categoryId: String
    ): Deferred<ApiResponse<PlanListResponse?>>


    //BUSINESS
    @GET("/api/business-category/search")
    fun getCategoriesAsync(
        @Query("search") keyword: String = ""
    ): Deferred<ApiResponse<GetCategoriesResponse?>>

    @GET("/api/business-category/featured")
    fun getFeaturedCategoriesAsync(
        @Query("search") keyword: String?
    ): Deferred<ApiResponse<GetCategoriesResponse?>>


    @GET("/api/business/search/")
    fun searchBusinessAsync(
        @Query("search") keyword: String?
    ): Deferred<ApiResponse<SearchBusinessResponse?>>

    @PUT("/api/business/")
    fun updateBusinessAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse?>>

    @POST("/api/business/")
    fun registerAsBusinessAsync(): Deferred<ApiResponse<EmptyResponse?>>

    @GET("/api/business/{businessId}")
    fun getBusinessDetailsAsync(@Path("businessId") businessId: String): Deferred<ApiResponse<BusinessDetailsResponse?>>

    @POST("/api/business/referral")
    fun applyReferralCodeAsync(@Body map: HashMap<String, String>): Deferred<ApiResponse<EmptyResponse?>>

    //QR Transaction
    @POST("/api/wallet-amt-transfer/generate")
    fun generateQrCodeAsync(): Deferred<ApiResponse<QrPaymentStatus?>>

    @POST("/api/wallet-amt-transfer/scan")
    fun scanQrCodeAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<ScanQrResponse?>>

    @POST("/api/wallet-amt-transfer/sender")
    fun checkQrTransferStatusSenderAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<QrStatusSenderResponse?>>

    @POST("/api/wallet-amt-transfer/receiver")
    fun checkQrTransferStatusReceiverAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<QrStatusRecieverResponse?>>

    @POST("/api/wallet-amt-transfer/payment-request")
    fun requestWalletPaymentAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<QrPaymentStatus?>>

    @POST("/api/wallet-amt-transfer/payment-confirm")
    fun confirmWalletPaymentAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<QrPaymentStatus?>>

    @POST("/api/wallet-amt-transfer/payment-cancel")
    fun cancelWalletPaymentAsync(
        @Body map: HashMap<String, Any?>
    ): Deferred<ApiResponse<EmptyResponse
    ?>>

    @GET("/api/wallet-amt-transfer/")
    fun getQrTransactionsAsync(
        @Query("timestamp") timestamp: String
    ): Deferred<ApiResponse<QrTransactionsResponse?>>

    @GET("/api/ad/")
    fun getAdsAsync(): Deferred<ApiResponse<GetAdsResponse?>>

}