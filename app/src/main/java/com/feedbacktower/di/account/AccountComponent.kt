package com.feedbacktower.di.account

import com.feedbacktower.ui.account.EditBusinessFragment
import com.feedbacktower.ui.account.find_customer.FindCustomerActivity
import com.feedbacktower.ui.account.business.AccountFragment
import com.feedbacktower.ui.account.customer.CustomerAccountFragment
import com.feedbacktower.ui.account.type_selection.AccountTypeSelectionFragment
import com.feedbacktower.ui.category.SelectCategoryFragment
import com.feedbacktower.ui.category.interests.SelectInterestsFragment
import com.feedbacktower.ui.city.SelectCityFragment
import com.feedbacktower.ui.location.live.TrackerService
import com.feedbacktower.ui.location.update.PointOnMapFragment
import dagger.Subcomponent

@AccountScope
@Subcomponent
interface AccountComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AccountComponent
    }

    fun inject(fragment: SelectCityFragment)
    fun inject(fragment: AccountFragment)
    fun inject(fragment: CustomerAccountFragment)
    fun inject(fragment: AccountTypeSelectionFragment)
    fun inject(fragment: EditBusinessFragment)
    fun inject(fragment: FindCustomerActivity)
    fun inject(fragment: SelectInterestsFragment)
    fun inject(fragment: SelectCategoryFragment)
    fun inject(fragment: PointOnMapFragment)
    fun inject(service: TrackerService)
}