package com.feedbacktower.ui.profile.setup2

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView
import java.io.File

interface BusinessSetup2Contract {
    interface View : BaseView {
        fun onDetailsUpdated(address: String, contact: String, website: String)
    }

    interface Presenter : BasePresenter<View> {
        fun updateDetails(
            address: String,
            contact: String,
            website: String
        )
    }
}