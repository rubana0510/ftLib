package com.feedbacktower.ui.profile.setup1

import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class BusinessSetup1Presenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<BusinessSetup1Contract.View>(),
    BusinessSetup1Contract.Presenter {
    override fun updateDetails(name: String, regNo: String, categoryId: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.updateBusinessAsync(
                hashMapOf(
                    "name" to name,
                    "regNo" to regNo,
                    "businessCategoryId" to categoryId
                )
            ).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            appPrefs.apply {
                user = user?.apply {
                    business = business?.apply {
                        this.name = name
                        this.regNo = regNo
                    }
                }
            }
            view?.onDetailsUpdated(name, regNo, categoryId)
        }
    }

    fun saveMasterCategory(selectedMasterCatId: String?) {
        appPrefs.setValue("MASTER_CAT_ID", selectedMasterCatId)
    }
}