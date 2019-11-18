package com.feedbacktower.di.suggestions

import com.feedbacktower.ui.suggestions.business.SuggestionsFragment
import com.feedbacktower.ui.suggestions.business.reply.ReplySuggestionDialog
import com.feedbacktower.ui.suggestions.my.MySuggestionsFragment
import dagger.Subcomponent

@SuggestionsScope
@Subcomponent
interface SuggestionsComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): SuggestionsComponent
    }

    fun inject(fragment: SuggestionsFragment)
    fun inject(fragment: MySuggestionsFragment)
    fun inject(fragment: ReplySuggestionDialog)
}