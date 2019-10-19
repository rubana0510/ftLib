package com.feedbacktower.ui.splash

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.BuildConfig
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.AppVersion
import com.feedbacktower.network.manager.AuthManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.notifications.FcmManager
import com.feedbacktower.ui.login.LoginScreen
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.logOut
import com.feedbacktower.util.navigateUser
import com.feedbacktower.util.showAppInStore


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fcmToken = AppPrefs.getInstance(this).firebaseToken
        pushFcmToken(fcmToken)
        subscribeToTopic()
    }

    private fun subscribeToTopic() {
        val user = AppPrefs.getInstance(this).user ?: return
        FcmManager.subscribeToTopic(user.userType)
    }

    override fun onResume() {
        super.onResume()
        if (AppPrefs.getInstance(this).user == null) {
            Handler().postDelayed({
                launchActivity<LoginScreen>()
                finish()
            }, 1000)
            return
        } else {
            refreshAuthToken()
        }
    }

    private fun pushFcmToken(token: String?) {
        token?.let {
            val lastToken = AppPrefs.getInstance(this).getValue("LAST_TOKEN_ON_SERVER")
            if (lastToken == token) return
            ProfileManager.getInstance()
                .updateFcmToken(token) { _, error ->
                    if (error != null) {
                        Log.e("SplashScreen", "Error: Token; ${error.message}")
                        return@updateFcmToken
                    }
                    AppPrefs.getInstance(this).setValue("LAST_TOKEN_ON_SERVER", token)
                    Log.d("SplashScreen", "Token Updated")
                }
        }
    }

    private fun refreshAuthToken() {
        Log.i("SplashScreen", "Refreshing token")
        AuthManager.getInstance().refreshToken()
        { response, error ->
            Log.d("SplashScreen", "Error Occurred: $error")
            if (error != null) {
                if (error.code == "USER_NOT_FOUND" || error.code == "INVALID_AUTH_TOKEN") {
                    logOut()
                    return@refreshToken
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Some error occurred")
                builder.setMessage(error.message)
                builder.setPositiveButton("TRY AGAIN") { _, _ -> refreshAuthToken() }
                builder.setCancelable(false)
                val alert = builder.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()

            }
            if (response != null) {
                AppPrefs.getInstance(this).apply {
                    user = response.user
                    authToken = response.token
                }
                Log.i("SplashScreen", "Token refreshed")
                AppPrefs.getInstance(this).latestVersionCode = response.appVersion.code
                //validate app version
                if (response.appVersion.code > BuildConfig.VERSION_CODE && response.appVersion.updateType == AppVersion.UpdateType.HARD) {
                    //user has old version
                    showForceUpdateDialog(response.appVersion.version)
                } else {
                    navigateUser(response.user)
                    finish()
                }
            } else {
                Log.e("SplashScreen", "Token refresh failed")
            }
        }
    }

    private fun showForceUpdateDialog(versionName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New update Available!")
        builder.setMessage("Download the latest version from play store.")
        builder.setPositiveButton("UPDATE") { _, _ -> showAppInStore() }
        val alert = builder.create()
        builder.setCancelable(false)
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

}
