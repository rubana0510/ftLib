package com.feedbacktower.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.feedbacktower.data.BusinessCategoriesRepository
import com.feedbacktower.data.models.BusinessCategory

class BusinessCategoriesViewModel internal  constructor(repository: BusinessCategoriesRepository): ViewModel(){
    private val categoryList = MediatorLiveData<List<BusinessCategory>>()

    init {
        val liveCategoryList = repository.getBusinessCategories()
        categoryList.addSource(liveCategoryList, categoryList::setValue)
    }

    fun getCategories() = categoryList
}