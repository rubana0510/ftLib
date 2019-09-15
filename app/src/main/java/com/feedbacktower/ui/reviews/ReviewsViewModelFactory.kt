package com.feedbacktower.ui.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feedbacktower.data.repository.HomeDataRepository
import com.feedbacktower.data.repository.reviews.ReviewsRepository

class ReviewsViewModelFactory(
    private val repository: ReviewsRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ReviewsViewModel(repository) as T
    }
}