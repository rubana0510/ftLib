package com.feedbacktower.di.home

import com.feedbacktower.ui.home.feed.HomeFragment
import dagger.Subcomponent

@HomeScope
@Subcomponent
interface HomeComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): HomeComponent
    }

    fun inject(fragment: HomeFragment)
}