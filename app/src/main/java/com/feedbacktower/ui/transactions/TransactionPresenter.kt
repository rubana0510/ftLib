package com.feedbacktower.ui.transactions

import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.ui.base.BasePresenterImpl

class TransactionPresenter : BasePresenterImpl<TransactionsContract.View>(),
    TransactionsContract.Presenter {
    override fun fetch(timestamp: String) {
        view?.showProgress()
        QRTransactionManager.getInstance().getTransactions(timestamp) { response, error ->
                view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@getTransactions
                }
                view?.onFetched(response, timestamp)
            }
    }
}