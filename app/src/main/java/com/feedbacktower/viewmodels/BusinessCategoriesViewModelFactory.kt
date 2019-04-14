package com.feedbacktower.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feedbacktower.data.BusinessCategoriesRepository

class BusinessCategoriesViewModelFactory(
    private val repository: BusinessCategoriesRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = BusinessCategoriesViewModel(repository) as T
}