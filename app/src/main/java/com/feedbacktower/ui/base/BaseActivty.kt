package com.feedbacktower.ui.base

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.Intent
import android.content.IntentFilter
import com.feedbacktower.R
import com.feedbacktower.ui.splash.SplashScreen
import com.feedbacktower.util.Constants
import com.feedbacktower.util.launchActivity
import org.jetbrains.anko.toast


open class BaseActivty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(sessionExpiredRx, IntentFilter(Constants.SESSION_EXPIRED_INTENT_FILTER))
    }

    private val sessionExpiredRx = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            launchActivity<SplashScreen> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}