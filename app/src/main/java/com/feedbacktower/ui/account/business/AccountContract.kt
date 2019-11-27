package com.feedbacktower.ui.account.business

import com.feedbacktower.network.models.MyBusinessResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface AccountContract {
    interface View : BaseView {
        fun onFetched(response: MyBusinessResponse?)
        fun onAvailabilityChanged(availability: Boolean)
        fun showAvailabilityChangeProgress()
        fun dismissAvailabilityChangeProgress()
        fun onLogOut()
    }

    interface Presenter : BasePresenter<View> {
        fun fetch()
        fun changeAvailability()
        fun logOut()
    }
}