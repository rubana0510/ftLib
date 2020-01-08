package com.feedbacktower.ui.home.post.text

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
class TextPostPresenter
@Inject constructor(
    private val apiService: ApiService
) : BasePresenterImpl<TextPostContract.View>(),
    TextPostContract.Presenter {
    override fun postText(caption: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.createTextPostAsync(hashMapOf("text" to caption)).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onTextPosted()
        }
    }

    override fun editPostText(caption: String, postId: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProgress()
            val response = apiService.editTextPostAsync(postId, hashMapOf("text" to caption)).awaitNetworkRequest()
            view?.dismissProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            view?.onTextEdited()
        }
    }
}