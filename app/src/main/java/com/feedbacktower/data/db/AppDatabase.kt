package com.feedbacktower.data.db

import android.content.Context
import androidx.room.*
import com.feedbacktower.data.db.dao.UserDao
import com.feedbacktower.data.models.User
import com.feedbacktower.util.Constants

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract var userDao: UserDao

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, Constants.DB_NAME)
                .build()
        }
    }
}
