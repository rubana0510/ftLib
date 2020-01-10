package com.feedbacktower.ui.myplan

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyPlanPresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<MyPlanContract.View>(),
    MyPlanContract.Presenter {
    override fun getMyPlans() {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.getTransactionsAsync().awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            response.payload?.let { view?.onMyPlansResponse(it) }
        }
    }
}