package com.feedbacktower.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.BuildConfig
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.AppVersion
import com.feedbacktower.network.manager.AuthManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.logOut
import com.feedbacktower.util.navigateUser
import com.feedbacktower.util.showAppInStore


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fcmToken = AppPrefs.getInstance(this).firebaseToken
        pushFcmToken(fcmToken)
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
        Log.d("SplashScreen", "Token is $token")
        token?.let {
            ProfileManager.getInstance()
                .updateFcmToken(token) { _, error ->
                    if (error != null) {
                        Log.e("SplashScreen", "Error: Token; ${error.message}")
                        return@updateFcmToken
                    }
                    Log.d("SplashScreen", "Token Updated")
                }
        }
    }

    private fun refreshAuthToken() {
        Log.i("SplashScreen", "Refreshing token")
        AuthManager.getInstance().refreshToken()
        { response, error ->
            if (error != null) {
                //toast(error.message ?: getString(R.string.default_err_message))
                //TODO: Must be handled using the error code  propagated from make request
                if (
                    error.message?.contains("USER") == true
                    && error.message?.contains("NOT") == true
                    && error.message?.contains("FOUND") == true
                ) {
                    logOut()
                    return@refreshToken
                }
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error occurred")
                builder.setMessage(error.message ?: getString(R.string.default_err_message))
                builder.setPositiveButton("TRY AGAIN") { _, _ -> refreshAuthToken() }
                builder.setCancelable(false)
                val alert = builder.create()
                alert.setCanceledOnTouchOutside(false)
                alert.show()
                return@refreshToken
            }
            if (response != null) {
                AppPrefs.getInstance(this).apply {
                    user = response.user
                    authToken = response.token
                }
                Log.i("SplashScreen", "Token refreshed")

                //validate app version
                if (response.appVersion.code > BuildConfig.VERSION_CODE) {
                    //user has old version
                    showUpdateDialog(response.appVersion)
                } else {
                    navigateUser(response.user)
                    finish()
                }
            } else {
                Log.e("SplashScreen", "Token refresh failed")
            }
        }
    }

    private fun showUpdateDialog(appVersion: AppVersion) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("New ${appVersion.version} Available!")
        builder.setMessage("Download the latest version from play store.")
        builder.setPositiveButton("DOWNLOAD") { _, _ -> showAppInStore() }
        if (appVersion.updateType == AppVersion.UpdateType.HARD)
            builder.setCancelable(false)
        val alert = builder.create()
        if (appVersion.updateType == AppVersion.UpdateType.HARD)
            alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

}
