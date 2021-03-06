package com.feedbacktower.ui.transactions

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TransactionPresenter @Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<TransactionsContract.View>(),
    TransactionsContract.Presenter {
    override fun fetch(timestamp: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.getQrTransactionsAsync(timestamp)
                .awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onFetched(response.payload, timestamp)
        }
    }
}