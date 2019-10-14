package com.feedbacktower.ui.transactions

import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class TransactionPresenter : BasePresenterImpl<TransactionsContract.View>(),
    TransactionsContract.Presenter {
    override fun fetch(timestamp: String) {
        getView()?.showProgress()
        QRTransactionManager.getInstance().getTransactions(timestamp) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getTransactions
                }
                getView()?.onFetched(response, timestamp)
            }
    }
}