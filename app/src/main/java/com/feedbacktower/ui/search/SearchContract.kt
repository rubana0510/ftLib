package com.feedbacktower.ui.search

import com.feedbacktower.network.models.SearchBusinessResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface SearchContract {
    interface View : BaseView {
        fun onFetched(response: SearchBusinessResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun fetch(keyword: String?)
    }
}