package com.feedbacktower.firestore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feedbacktower.data.models.BusinessCategory

class FirestoreManager private constructor() {
    companion object {
        @Volatile
        private var instance: FirestoreManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FirestoreManager().also { instance = it }
            }
    }

    fun getBusinessCategories(): LiveData<List<BusinessCategory>> {
        val list = MutableLiveData<List<BusinessCategory>>()

        return list
    }

}