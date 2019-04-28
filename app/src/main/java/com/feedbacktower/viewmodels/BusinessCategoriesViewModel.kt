package com.feedbacktower.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feedbacktower.data.BusinessCategoriesRepository
import com.feedbacktower.data.models.BusinessCategory
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class BusinessCategoriesViewModel internal constructor(private val repository: BusinessCategoriesRepository) : ViewModel() {
    private val parentJob = Job()

    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default

    private val scope = CoroutineScope(coroutineContext)



    val categoriesLiveData = MutableLiveData<MutableList<BusinessCategory>>()

    fun fetchBusinessCategories() {
        scope.launch {
            val categoryList = repository.getBusinessCategoriesAsync()
            categoriesLiveData.postValue(categoryList)
        }
    }


    fun cancelAllRequests() = coroutineContext.cancel()
}