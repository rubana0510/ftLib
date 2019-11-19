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
            getView()?.showProgress()
            val response = apiService.createVideoPostAsync(
                file.toPart(),
                caption.toRequestBody()
            ).awaitNetworkRequest()
            getView()?.dismissProgress()
            if (response.error != null) {
                getView()?.showNetworkError(response.error)
                return@launch
            }
            getView()?.onVideoPosted()
        }
    }
}