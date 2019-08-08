package com.feedbacktower.data.db

import android.content.Context
import androidx.room.*
import com.feedbacktower.data.db.dao.AdsDao
import com.feedbacktower.data.db.dao.UserDao
import com.feedbacktower.data.models.Ad
import com.feedbacktower.data.models.User
import com.feedbacktower.util.Constants

@Database(entities = [Ad::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adsDao(): AdsDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context): AppDatabase {
            return instance ?: synchronized(LOCK) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constants.DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
