package com.feedbacktower.ui.search

import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class SearchPresenter : BasePresenterImpl<SearchContract.View>(),
    SearchContract.Presenter {
    override fun fetch(keyword: String?){
        view?.showProgress()
        ProfileManager.getInstance()
            .searchBusiness(keyword)  { response, error ->
                view?.dismissProgress()
                if (error != null) {
                    view?.showNetworkError(error)
                    return@searchBusiness
                }
                view?.onFetched(response)
            }
    }
}