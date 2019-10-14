package com.feedbacktower.ui.search

import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class SearchPresenter : BasePresenterImpl<SearchContract.View>(),
    SearchContract.Presenter {
    override fun fetch(keyword: String?){
        getView()?.showProgress()
        ProfileManager.getInstance()
            .searchBusiness(keyword)  { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@searchBusiness
                }
                getView()?.onFetched(response)
            }
    }
}