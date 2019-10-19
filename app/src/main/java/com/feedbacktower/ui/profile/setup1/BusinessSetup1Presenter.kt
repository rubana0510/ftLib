package com.feedbacktower.ui.profile.setup1

import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class BusinessSetup1Presenter : BasePresenterImpl<BusinessSetup1Contract.View>(),
    BusinessSetup1Contract.Presenter {
    override fun updateDetails(name: String, regNo: String, categoryId: String?) {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .updateBusinessBasicDetails(name, regNo, categoryId) { _, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@updateBusinessBasicDetails
                }
                getView()?.onDetailsUpdated(name, regNo, categoryId)
            }
    }
}