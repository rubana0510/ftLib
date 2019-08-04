package com.feedbacktower.data.repository

import androidx.lifecycle.LiveData
import com.feedbacktower.data.models.Ad

interface HomeDataRepository {
    suspend fun fetchAds(): LiveData<List<Ad>>
}