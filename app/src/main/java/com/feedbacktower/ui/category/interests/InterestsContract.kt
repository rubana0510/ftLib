package com.feedbacktower.ui.category.interests

import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.network.models.GetCategoriesResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface InterestsContract {
    interface View : BaseView {
        fun onFetched(offset: Int, response: GetCategoriesResponse?)
        fun onToggled()
    }

    interface Presenter : BasePresenter<View> {
        fun fetch(offset: Int = 0)
        fun toggleInterest(interest: BusinessCategory)
    }
}