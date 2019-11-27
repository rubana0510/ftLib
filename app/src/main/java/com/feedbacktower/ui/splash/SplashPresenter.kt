package com.feedbacktower.ui.splash

import android.os.Handler
import com.feedbacktower.BuildConfig
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.AppVersion
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.notifications.FcmManager
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<SplashContract.View>(), SplashContract.Presenter {
    override fun refreshToken() {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.refreshTokenAsync().awaitNetworkRequest()
            if (response.error != null) {
                getView()?.tokenRefreshError(response.error)
                return@launch
            }
            response.payload?.let { data ->
                appPrefs.apply {
                    user = data.user
                    authToken = data.token
                }
                appPrefs.latestVersionCode = data.appVersion.code
                //validate app version
                if (data.appVersion.code > BuildConfig.VERSION_CODE && data.appVersion.updateType == AppVersion.UpdateType.HARD) {
                    //user has old version
                    getView()?.forceUpdateRequired(data.appVersion)
                } else {
                    getView()?.onRefreshed(data)
                }
            }

        }
    }

    override fun saveFirebaseToken() {
        GlobalScope.launch(Dispatchers.Main) {
            if (appPrefs.tokenPushRequired) {
                appPrefs.firebaseToken?.let { token ->
                    val response =
                        apiService.updatePersonalDetailsAsync(hashMapOf("fcmToken" to token)).awaitNetworkRequest()
                    if (response.error != null) {
                        // getView()?.showNetworkError(response.error)
                        return@launch
                    }
                    appPrefs.tokenPushRequired = false
                }
            }
        }
    }

    override fun subscribeToTopicNotifications() {
        appPrefs.user?.let { user ->
            FcmManager.subscribeToTopic(user.userType)
        }
    }

    override fun checkUserSignedIn() {
        if (appPrefs.user == null) {
            Handler().postDelayed({
                getView()?.loginRequired()
            }, 1000)
            return
        } else {
            refreshToken()
        }
    }
}