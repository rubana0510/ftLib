package com.feedbacktower.ui.category

import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class CategoryPresenter : BasePresenterImpl<CategoryContract.View>(),
    CategoryContract.Presenter {
    override fun fetch(keyword: String) {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .getAllCategories(keyword) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getAllCategories
                }
                getView()?.onFetched(response)
            }
    }
}