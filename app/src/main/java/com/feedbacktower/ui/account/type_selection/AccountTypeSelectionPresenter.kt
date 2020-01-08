package com.feedbacktower.ui.account.type_selection

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountTypeSelectionPresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<AccountTypeSelectionContract.View>(),
    AccountTypeSelectionContract.Presenter {

    override fun continueAsCustomer() {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.continueAsCustomerAsync().awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onContinueCustomerResponse()
        }
    }

    override fun registerAsBusiness() {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.continueAsCustomerAsync().awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onBusinessRegistered()
        }
    }
}