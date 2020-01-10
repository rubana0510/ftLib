package com.feedbacktower.di.reviews
import com.feedbacktower.ui.reviews.business.ReviewsFragment
import com.feedbacktower.ui.reviews.my.MyReviewsFragment
import com.feedbacktower.ui.reviews.send.RateReviewDialog
import dagger.Subcomponent

@ReviewsScope
@Subcomponent
interface ReviewsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ReviewsComponent
    }
    fun inject(fragment: ReviewsFragment)
    fun inject(fragment: MyReviewsFragment)
    fun inject(fragment: RateReviewDialog)
}