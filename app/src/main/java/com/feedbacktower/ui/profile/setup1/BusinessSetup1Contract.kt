package com.feedbacktower.ui.profile.setup1

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView
import java.io.File

interface BusinessSetup1Contract {
    interface View : BaseView {
        fun onDetailsUpdated(
            name: String,
            regNo: String,
            categoryId: String?
        )
    }

    interface Presenter : BasePresenter<View> {
        fun updateDetails(
            name: String,
            regNo: String,
            categoryId: String?
        )
    }
}