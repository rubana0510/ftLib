package com.feedbacktower.ui.profile.setup2

import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class BusinessSetup2Presenter : BasePresenterImpl<BusinessSetup2Contract.View>(),
    BusinessSetup2Contract.Presenter {
    override fun updateDetails(address: String, contact: String, website: String) {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .updateBusinessAddressDetails(address, contact, website, null, null) { _, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@updateBusinessAddressDetails
                }
                getView()?.onDetailsUpdated(address, contact, website)
            }
    }
}