package com.feedbacktower.data

import androidx.lifecycle.LiveData
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.firestore.FirestoreManager

class BusinessCategoriesRepository private constructor(val manager: FirestoreManager){

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: BusinessCategoriesRepository? = null

        fun getInstance(manager: FirestoreManager) =
            instance ?: synchronized(this) {
                instance ?: BusinessCategoriesRepository(manager).also { instance = it }
            }
    }

    fun getBusinessCategories() = manager.getBusinessCategories()
}