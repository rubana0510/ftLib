package com.feedbacktower.di.account

import com.feedbacktower.ui.account.EditBusinessFragment
import com.feedbacktower.ui.account.FindCustomerActivity
import com.feedbacktower.ui.account.business.AccountFragment
import com.feedbacktower.ui.account.customer.CustomerAccountFragment
import com.feedbacktower.ui.account.type_selection.AccountTypeSelectionFragment
import com.feedbacktower.ui.category.SelectCategoryFragment
import com.feedbacktower.ui.category.interests.SelectInterestsFragment
import com.feedbacktower.ui.city.SelectCityFragment
import com.feedbacktower.ui.suggestions.business.SuggestionsFragment
import com.feedbacktower.ui.suggestions.business.reply.ReplySuggestionDialog
import com.feedbacktower.ui.suggestions.my.MySuggestionsFragment
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
}