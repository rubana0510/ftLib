package com.feedbacktower.ui.home.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedbacktower.data.models.Post
import com.feedbacktower.network.models.GetAdsResponse
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LastLikedPost(val position: Int, val liked: Boolean)

class HomeViewModel constructor(
    private val apiService: ApiService
) : ViewModel() {
    private val _posts = MutableLiveData<List<Post>>()

    val posts: LiveData<List<Post>>
        get() = _posts

    private val _ads = MutableLiveData<GetAdsResponse>()

    val ads: LiveData<GetAdsResponse>
        get() = _ads

    private val _lastLikedPost = MutableLiveData<LastLikedPost>()

    val lastLikedPost: LiveData<LastLikedPost>
        get() = _lastLikedPost

    private val _loading = MutableLiveData<Boolean>()

    val loading: LiveData<Boolean>
        get() = _loading

    var currentTimeStamp: String? = null
        private set

    private val _postList: ArrayList<Post> = ArrayList()

    val postList: ArrayList<Post>
        get() = _postList

     var postsOver: Boolean = false


    fun getPosts(timestamp: String? = null) {
        currentTimeStamp = timestamp
        GlobalScope.launch(Dispatchers.Main) {
            _loading.postValue(true)
            val response = apiService.getPostsAsync(timestamp).awaitNetworkRequest()
            if (response.error != null) {
                return@launch
            }
            _loading.postValue(false)
            response.payload?.let {
                postsOver = it.posts.size < Constants.PAGE_SIZE
                if (timestamp.isNullOrEmpty())
                    _postList.clear()
                _postList.addAll(it.posts)
                _posts.postValue(_postList)
            }
        }
    }

    fun getAds() {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.getAdsAsync().awaitNetworkRequest()
            if (response.error != null) {
                return@launch
            }
            _ads.postValue(response.payload)
        }
    }

    fun likePost(postId: String, position: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.likePostAsync(postId).awaitNetworkRequest()
            if (response.error != null) {
                return@launch
            }
            response.payload?.let {
                _lastLikedPost.postValue(LastLikedPost(position, it.liked))
            }
        }
    }

}