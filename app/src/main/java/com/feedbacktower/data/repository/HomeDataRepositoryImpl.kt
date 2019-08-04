package com.feedbacktower.data.repository

import androidx.lifecycle.LiveData
import com.feedbacktower.data.db.dao.AdsDao
import com.feedbacktower.data.models.Ad
import com.feedbacktower.data.network.HomeDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeDataRepositoryImpl(
    private val homeDataSource: HomeDataSource,
    private val adsDao: AdsDao
) : HomeDataRepository {

    init {
        homeDataSource.ads.observeForever { ads ->
            saveAds(ads)
        }
    }

    override suspend fun fetchAds(): LiveData<List<Ad>> = withContext(Dispatchers.IO) {
        initFetchAds()
        adsDao.getAll()
    }

    private suspend fun initFetchAds() {
        homeDataSource.fetchAds()
    }


    private fun saveAds(banners: List<Ad>?) {
        GlobalScope.launch(Dispatchers.IO) {
            banners?.let {
                adsDao.save(it)
            }
        }
    }
}
