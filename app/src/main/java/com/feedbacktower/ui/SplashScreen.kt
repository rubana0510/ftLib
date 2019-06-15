package com.feedbacktower.ui

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.network.manager.AuthManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.navigateUser

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = AppPrefs.getInstance(this).user
        val fcmToken = AppPrefs.getInstance(this).firebaseToken
        pushFcmToken(fcmToken)
        Log.d("SplashScreen", "User: $user")
        if (user == null) {
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
               val builder =  AlertDialog.Builder(this)
                builder.setTitle("Error occurred")
                builder.setMessage(error.message ?: getString(R.string.default_err_message))
                builder.setPositiveButton("TRY AGAIN") { _, _-> refreshAuthToken()}
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
                Handler().postDelayed({
                    navigateUser(response.user)
                    finish()
                }, 1000)
            } else {
                Log.e("SplashScreen", "Token refresh failed")
            }

        }
    }

}
