package com.feedbacktower.ui.myplan

import com.feedbacktower.network.models.PlanTransactionsResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface MyPlanContract {
    interface View : BaseView {
        fun onMyPlansResponse(response: PlanTransactionsResponse)
    }

    interface Presenter : BasePresenter<View> {
        fun getMyPlans()
    }
}