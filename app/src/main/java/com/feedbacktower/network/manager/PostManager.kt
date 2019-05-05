package com.feedbacktower.network.manager

import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    fun createTextPost(text: String, onComplete: (EmptyResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.createTextPostAsync(hashMapOf("text" to text))
                .makeRequest(onComplete)
        }
    }

}