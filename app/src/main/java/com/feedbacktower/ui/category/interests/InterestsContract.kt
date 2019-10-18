package com.feedbacktower.ui.category.interests

import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.network.models.GetCategoriesResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface InterestsContract {
    interface View : BaseView {
        fun onFetched(response: GetCategoriesResponse?)
        fun onToggled()
    }

    interface Presenter : BasePresenter<View> {
        fun fetch(keyword: String? = null)
        fun toggleInterest(interest: BusinessCategory)
    }
}