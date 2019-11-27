package com.feedbacktower.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.feedbacktower.App
import com.feedbacktower.data.models.AppVersion
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.AuthResponse
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.ui.login.LoginScreen
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.logOut
import com.feedbacktower.util.navigateUser
import com.feedbacktower.util.showAppInStore
import javax.inject.Inject


class SplashScreen : BaseViewActivityImpl(), SplashContract.View {
    @Inject
    lateinit var presenter: SplashPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as App).appComponent.authComponent().create()
        presenter.attachView(this)
        presenter.saveFirebaseToken()
        presenter.subscribeToTopicNotifications()
    }

    override fun onResume() {
        super.onResume()
        presenter.checkUserSignedIn()
    }

    override fun tokenRefreshError(error: ApiResponse.ErrorModel) {
        if (error.code == "USER_NOT_FOUND" || error.code == "INVALID_AUTH_TOKEN") {
            logOut()
            return
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Some error occurred")
        builder.setMessage(error.message)
        builder.setPositiveButton("TRY AGAIN") { _, _ -> presenter.refreshToken() }
        builder.setCancelable(false)
        val alert = builder.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    override fun onRefreshed(response: AuthResponse) {
        navigateUser(response.user)
        finish()
    }

    override fun forceUpdateRequired(version: AppVersion) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New update Available!")
        builder.setMessage("Download the latest version from play store.")
        builder.setPositiveButton("UPDATE") { _, _ -> showAppInStore() }
        val alert = builder.create()
        builder.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    override fun loginRequired() {
        launchActivity<LoginScreen>()
        finish()
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }

}
