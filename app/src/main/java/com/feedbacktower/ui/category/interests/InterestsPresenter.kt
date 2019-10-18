package com.feedbacktower.ui.category.interests

import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl

class InterestsPresenter : BasePresenterImpl<InterestsContract.View>(),
    InterestsContract.Presenter {
    override fun toggleInterest(interest: BusinessCategory) {
        ProfileManager.getInstance()
            .setUnsetCategoryInterest(interest.id, interest.selected) { _, error ->
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@setUnsetCategoryInterest
                }
                getView()?.onToggled()
            }
    }

    override fun fetch(keyword: String?) {
        getView()?.showProgress()
        ProfileManager.getInstance()
            .getFeaturedCategories(keyword) { response, error ->
                getView()?.dismissProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@getFeaturedCategories
                }
                getView()?.onFetched(response)
            }
    }
}