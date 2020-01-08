package com.feedbacktower.ui.home.post.video

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
class VideoPostPresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<VideoPostContract.View>(),
    VideoPostContract.Presenter {
    override fun postVideo(file: File, caption: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.createVideoPostAsync(
                file.toPart(),
                caption.toRequestBody()
            ).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onVideoPosted()
        }
    }
}