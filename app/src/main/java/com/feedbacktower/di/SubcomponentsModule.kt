package com.feedbacktower.di
import com.feedbacktower.di.reviews.ReviewsComponent
import dagger.Module

@Module(subcomponents = [ReviewsComponent::class])
class SubcomponentsModule {}