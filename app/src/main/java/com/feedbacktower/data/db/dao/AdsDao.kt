package com.feedbacktower.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.feedbacktower.data.models.Ad

@Dao
interface AdsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(ad: Ad)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(ads: List<Ad>)

    @Query("SELECT * FROM ads")
    fun getAll(): LiveData<List<Ad>>

    @Query("DELETE  FROM ads")
    fun deleteAll()
}