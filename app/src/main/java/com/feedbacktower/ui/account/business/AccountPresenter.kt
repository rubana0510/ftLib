package com.feedbacktower.ui.account.business

import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class AccountPresenter : BasePresenterImpl<AccountContract.View>(),
    AccountContract.Presenter {
    override fun changeAvailability(availability: Boolean) {
        getView()?.showAvailabilityChangeProgress()
        ProfileManager.getInstance()
            .changeBusinessAvailability(availability) { _, error ->
                getView()?.dismissAvailabilityChangeProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@changeBusinessAvailability
                }
                getView()?.onAvailabilityChanged(availability)
            }
    }

    override fun fetch() {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .getMyBusiness { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getMyBusiness
                }
                getView()?.onFetched(response)
            }
    }
}