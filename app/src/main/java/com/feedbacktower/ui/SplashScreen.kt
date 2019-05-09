package com.feedbacktower.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.feedbacktower.BusinessMainActivity
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.User
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.navigateUser

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val user = AppPrefs.getInstance(this).user
        Log.d("SplashScreen", "User: $user")
        if (user == null) {
            Handler().postDelayed({
                launchActivity<LoginScreen>()
                finish()
            }, 1000)
            return
        }
        Handler().postDelayed({
            navigateUser(user)
            finish()
        }, 1000)
    }

}
