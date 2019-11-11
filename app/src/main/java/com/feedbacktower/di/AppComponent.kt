package com.feedbacktower.di

import android.content.Context
import com.feedbacktower.di.reviews.ReviewsComponent
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, StorageModule::class, SubcomponentsModule::class])
interface AppComponent {

    // Factory to create instances of the AppComponent
    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }
    fun reviewComponent(): ReviewsComponent.Factory
}