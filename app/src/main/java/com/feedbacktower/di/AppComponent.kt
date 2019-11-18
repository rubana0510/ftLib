package com.feedbacktower.di

import android.content.Context
import com.feedbacktower.di.account.AccountComponent
import com.feedbacktower.di.business_detail.BusinessDetailComponent
import com.feedbacktower.di.home.HomeComponent
import com.feedbacktower.di.reviews.ReviewsComponent
import com.feedbacktower.di.suggestions.SuggestionsComponent
import com.feedbacktower.di.upload_post.UploadPostComponent
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
    fun suggestionComponent(): SuggestionsComponent.Factory
    fun businessDetailComponent(): BusinessDetailComponent.Factory
    fun accountComponent(): AccountComponent.Factory
    fun homeComponent(): HomeComponent.Factory
    fun uploadPostComponent(): UploadPostComponent.Factory
}