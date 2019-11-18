package com.feedbacktower.ui.home.post.image

import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import com.feedbacktower.util.toPart
import com.feedbacktower.util.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagePostPresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<ImagePostContract.View>(),
    ImagePostContract.Presenter {
    override fun postImage(file: File, caption: String) {
        GlobalScope.launch(Dispatchers.Main) {
            getView()?.showProgress()
            val response = apiService.createPhotoPostAsync(
                file.toPart(),
                caption.toRequestBody()
            ).awaitNetworkRequest()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onImagePosted()
        }
    }
}