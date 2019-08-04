package com.feedbacktower.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feedbacktower.data.repository.HomeDataRepository

class HomeViewModelFactory(
    private val homeDataRepository: HomeDataRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(homeDataRepository) as T
    }
}