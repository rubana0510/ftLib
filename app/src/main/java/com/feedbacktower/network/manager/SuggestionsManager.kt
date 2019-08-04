package com.feedbacktower.network.manager

import com.feedbacktower.network.models.*
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SuggestionsManager {
    private val TAG = "SuggestionsManager"
    private val apiService: ApiService by lazy {
        ApiService.create()
    }

    companion object {
        @Volatile
        private var instace: SuggestionsManager? = null

        fun getInstance(): SuggestionsManager =
            instace ?: synchronized(this) {
                instace ?: SuggestionsManager().also { instace = it }
            }
    }

    fun getSuggestions(timestamp: String, onComplete: (GetSuggestionsResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getSuggestionsAsync(timestamp)
                .makeRequest(onComplete)
        }
    }

    fun getMySuggestions(timestamp: String, onComplete: (GetSuggestionsResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.getMySuggestionsAsync(timestamp)
                .makeRequest(onComplete)
        }
    }
    fun addSuggestion(payload: HashMap<String, Any?>, onComplete: (EmptyResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.addSuggestionAsync(payload)
                .makeRequest(onComplete)
        }
    }
    fun replySuggestion(id: String, reply: String, onComplete: (EmptyResponse?, Throwable?) -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            apiService.replySuggestionAsync(
                hashMapOf("id" to id, "reply" to reply)
            ).makeRequest(onComplete)
        }
    }


}