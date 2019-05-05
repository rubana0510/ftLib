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
        val user = AppPrefs.getInstance(this).user
        if (user == null) {
            Handler().postDelayed({
                launchActivity<LoginScreen>()
                finish()
            }, 1000)
        } else {
            if (!user.profileSetup)
                launchActivity<ProfileSetupScreen>()
            else if (user.business == null)
                launchActivity<CustomerMainActivity>()
            else
                launchActivity<BusinessProfileSetupScreen> { }

            finish()
        }
    }
}
