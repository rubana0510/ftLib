package com.feedbacktower.ui.account.find_customer

import android.graphics.Bitmap
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import com.feedbacktower.util.toQrBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FindCustomerPresenter
@Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<FindCustomerContract.View>(),
    FindCustomerContract.Presenter {
    override fun findCustomer(qrData: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.findCustomerAsync(qrData).awaitNetworkRequest()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            response.payload?.let {
                getView()?.onFoundResponse(it)
            }
        }
    }

    fun getMyQrBitmap(): Bitmap?{
        return appPrefs.user?.id?.toQrBitmap()
    }
}