package com.feedbacktower.data.network

import androidx.lifecycle.LiveData
import com.feedbacktower.data.models.Ad

interface HomeDataSource {
    val ads: LiveData<List<Ad>>
    suspend fun fetchAds()
}