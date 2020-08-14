package com.feedbacktower.ui.home.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feedbacktower.network.service.ApiService

class ViewModelFactory constructor(private val apiService: ApiService) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}