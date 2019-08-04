package com.feedbacktower.ui.home

import androidx.lifecycle.ViewModel
import com.feedbacktower.data.repository.HomeDataRepository

class HomeViewModel(
    private val repository: HomeDataRepository
) : ViewModel() {
    suspend fun getBanners() = repository.fetchAds()
}