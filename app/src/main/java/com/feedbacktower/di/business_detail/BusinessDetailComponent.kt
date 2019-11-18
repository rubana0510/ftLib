package com.feedbacktower.di.business_detail

import com.feedbacktower.ui.business_detail.BusinessDetailFragment
import dagger.Subcomponent

@BusinessDetailScope
@Subcomponent
interface BusinessDetailComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): BusinessDetailComponent
    }

    fun inject(fragment: BusinessDetailFragment)
}