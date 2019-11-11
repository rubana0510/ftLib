package com.feedbacktower.di
import android.content.Context
import com.feedbacktower.data.ApplicationPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {
    @Singleton
    @Provides
    fun provideAppPrefs(context: Context): ApplicationPreferences {
        return ApplicationPreferences(context)
    }
}