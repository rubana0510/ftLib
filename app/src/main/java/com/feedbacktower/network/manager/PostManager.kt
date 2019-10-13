package com.feedbacktower.network.manager

import android.util.Log
import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeRequest
import com.feedbacktower.util.getMimeType
import com.feedbacktower.util.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class PostManager {
    private val TAG = "PostManager"
    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instace: PostManager? = null

        fun getInstance(): PostManager =
            instace ?: synchronized(this) {
                instace ?: PostManager().also { instace = it }
            }
    }

    fun getPosts(timestamp: String?, onComplete: (GetPostsResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getPostsAsync(timestamp)
                .makeRequest(onComplete)
        }
    }

    fun getBusinessPosts(
        businessId: String,
        timestamp: String,
        onComplete: (GetPostsResponse?, ApiResponse.ErrorModel?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getBusinessPostsAsync(businessId, timestamp)
                .makeRequest(onComplete)
        }
    }


    fun createTextPost(text: String, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.createTextPostAsync(hashMapOf("text" to text))
                .makeRequest(onComplete)
        }
    }

    fun editTextPost(postId: String, text: String, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.editTextPostAsync(postId, hashMapOf("text" to text))
                .makeRequest(onComplete)
        }
    }

    fun createPhotoPost(text: String, file: File, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {

        if (!file.exists()) {
            Log.e(TAG, "uploadImages: FileNotFound")
            return
        }
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val filePart = MultipartBody.Part.createFormData("media", file.name, requestBody)
        GlobalScope.launch(Dispatchers.Main) {
            apiService.createPhotoPostAsync(
                filePart,
                text.toRequestBody()
            ).makeRequest(onComplete)
        }
    }

    fun createVideoPost(text: String, file: File, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {

        if (!file.exists()) {
            Log.e(TAG, "uploadImages: FileNotFound")
            return
        }
        val requestBody = RequestBody.create(MediaType.parse(file.getMimeType() ?: "video/mp4"), file)
        val filePart = MultipartBody.Part.createFormData("media", "POST_${System.currentTimeMillis()}", requestBody)
        GlobalScope.launch(Dispatchers.Main) {
            apiService.createVideoPostAsync(
                filePart,
                text.toRequestBody()
            ).makeRequest(onComplete)
        }
    }

    fun likePost(businessId: String, onComplete: (LikeUnlikeResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.likePostAsync(businessId)
                .makeRequest(onComplete)
        }
    }

    fun deletePost(postId: String, onComplete: (EmptyResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.deletePost(postId)
                .makeRequest(onComplete)
        }
    }

    fun getAds(onComplete: (GetAdsResponse?, ApiResponse.ErrorModel?) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            apiService.getAdsAsync()
                .makeRequest(onComplete)
        }
    }
}
