package com.feedbacktower.ui

import android.content.Intent
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
            if (user.userType == "CUSTOMER") {
                if (!user.profileSetup)
                    launchActivity<ProfileSetupScreen>() {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                else if (user.business == null)
                    launchActivity<CustomerMainActivity>() {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                else
                    launchActivity<BusinessProfileSetupScreen> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
            } else if (user.userType == "BUSINESS") {
                launchActivity<BusinessMainActivity> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            finish()
        }
    }
}
