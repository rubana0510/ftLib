package com.feedbacktower.di.reviews
import com.feedbacktower.ui.reviews.business.ReviewsFragment
import dagger.Subcomponent

@ReviewsScope
@Subcomponent
interface ReviewsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ReviewsComponent
    }
    fun inject(fragment: ReviewsFragment)
}