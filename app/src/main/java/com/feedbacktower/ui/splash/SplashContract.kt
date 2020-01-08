package com.feedbacktower.ui.splash

import com.feedbacktower.data.models.AppVersion
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.AuthResponse
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface SplashContract {
    interface Presenter : BasePresenter<View> {
        fun refreshToken()
        fun saveFirebaseToken()
        fun subscribeToTopicNotifications()
        fun checkUserSignedIn()
    }

    interface View : BaseView {
        fun onRefreshed(response: AuthResponse)
        fun forceUpdateRequired(version: AppVersion)
        fun tokenRefreshError(error: ApiResponse.ErrorModel)
        fun loginRequired()
        fun logout()
    }
}
