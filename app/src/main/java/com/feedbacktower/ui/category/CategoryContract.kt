package com.feedbacktower.ui.category

import com.feedbacktower.network.models.GetCategoriesResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface CategoryContract {
    interface View : BaseView {
        fun onFetched(keyword: String, offset: Int,response: GetCategoriesResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun fetch(keyword: String, offset: Int)
    }
}