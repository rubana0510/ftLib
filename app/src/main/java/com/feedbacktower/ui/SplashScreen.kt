package com.feedbacktower.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.feedbacktower.BusinessMainActivity
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.util.launchActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppPrefs.getInstance(this).authToken != null) {
            Handler().postDelayed({
                launchActivity<BusinessMainActivity>()
                finish()
            }, 1000)
        } else {
            Handler().postDelayed({
                launchActivity<LoginScreen>()
                finish()
            }, 1000)
        }
    }
}
