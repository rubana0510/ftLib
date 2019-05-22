package com.feedbacktower.network.manager

import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toFile
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

class PostManager {
    private val TAG = "PostManager"
    private val apiService: ApiServiceDescriptor by lazy {
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

    fun getPosts(timestamp: String, type: String, onComplete: (GetPostsResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getPostsAsync(timestamp, type)
                .makeRequest(onComplete)
        }
    }

    fun getBusinessPosts(
        businessId: String,
        timestamp: String,
        type: String,
        onComplete: (GetPostsResponse?, Throwable?) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getBusinessPostsAsync(businessId, timestamp, type)
                .makeRequest(onComplete)
        }
    }


    fun createTextPost(text: String, onComplete: (EmptyResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.createTextPostAsync(hashMapOf("text" to text))
                .makeRequest(onComplete)
        }
    }

    fun createPhotoPost(text: String, file: File, onComplete: (EmptyResponse?, Throwable?) -> Unit) {

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

    fun createVideoPost(text: String, file: File, onComplete: (EmptyResponse?, Throwable?) -> Unit) {

        if (!file.exists()) {
            Log.e(TAG, "uploadImages: FileNotFound")
            return
        }
        val requestBody = RequestBody.create(MediaType.parse(file.getMimeType()?:"video/mp4"), file)
        val filePart = MultipartBody.Part.createFormData("media", file.name, requestBody)
        GlobalScope.launch(Dispatchers.Main) {
            apiService.createVideoPostAsync(
                filePart,
                text.toRequestBody()
            ).makeRequest(onComplete)
        }
    }

    fun likePost(businessId: String, onComplete: (LikeUnlikeResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.likePostAsync(businessId)
                .makeRequest(onComplete)
        }
    }

}

private fun File.getMimeType(): String? {
    val uri = Uri.fromFile(this)
    val ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    return  MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
}
