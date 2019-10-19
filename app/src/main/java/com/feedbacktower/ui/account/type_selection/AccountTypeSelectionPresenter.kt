package com.feedbacktower.ui.account.type_selection

import com.feedbacktower.network.manager.AuthManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class AccountTypeSelectionPresenter : BasePresenterImpl<AccountTypeSelectionContract.View>(),
    AccountTypeSelectionContract.Presenter {

    override fun continueAsCustomer() {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .continueAsCustomer onResponse@{ _, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@onResponse
                }
                getView()?.onContinueCustomerResponse()
            }
    }

    override fun registerAsBusiness() {
        getView()?.showProgress()
        AuthManager.getInstance()
            .registerAsBusiness onResponse@{ _, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@onResponse
                }
                getView()?.onBusinessRegistered()
            }
    }
}