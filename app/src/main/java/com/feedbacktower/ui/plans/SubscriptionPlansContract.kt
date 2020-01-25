package com.feedbacktower.ui.plans

import com.feedbacktower.data.models.Plan
import com.feedbacktower.network.models.GenerateHashRequest
import com.feedbacktower.network.models.GenerateHashResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface SubscriptionPlansContract {
    interface View : BaseView {
        fun onPlansResponse(list: List<Plan>?)
        fun onTxnCancelled()
        fun onHashGenerated(request: GenerateHashRequest, response: GenerateHashResponse, plan: Plan)
    }

    interface Presenter : BasePresenter<View> {
        fun getSubscriptionPlans(categoryId: String)
        fun cancelTxn(id: String)
        fun generateHash(request: GenerateHashRequest, plan: Plan)
    }
}