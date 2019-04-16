package com.feedbacktower.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.feedbacktower.BusinessMainActivity
import com.feedbacktower.util.launchActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            launchActivity<BusinessMainActivity>()
        }, 1000)
    }
}
