

package com.feedbacktower.util

import android.content.Context
import com.feedbacktower.data.BusinessCategoriesRepository
import com.feedbacktower.firestore.FirestoreManager
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.viewmodels.BusinessCategoriesViewModelFactory

/**
 * Static methods used to inject classes needed for various Activities and Fragments.
 */
object InjectorUtils {

    private fun getBusinessCategoriesRepository(context: Context): BusinessCategoriesRepository {
        return BusinessCategoriesRepository.getInstance()
    }


    fun provideBusinessCategoriesViewModelFactory(
        context: Context
    ): BusinessCategoriesViewModelFactory {
        val repository = getBusinessCategoriesRepository(context)
        return BusinessCategoriesViewModelFactory(repository)
    }
}
