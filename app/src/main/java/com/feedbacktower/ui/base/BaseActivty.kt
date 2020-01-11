package com.feedbacktower.ui.base

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.feedbacktower.R
import com.feedbacktower.ui.splash.SplashScreen
import com.feedbacktower.util.Constants
import com.feedbacktower.util.launchActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast


open class BaseActivty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(sessionExpiredRx, IntentFilter(Constants.SESSION_EXPIRED_INTENT_FILTER))
    }

    private val sessionExpiredRx = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //TODO: Try to show alert
           /* val alert = AlertDialog.Builder(this@BaseActivty)
                .setTitle("Session expired")
                .setMessage("Your session has expired, please login again")
                .setPositiveButton("OKAY") { _, _ ->
                    launchActivity<SplashScreen> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }
                .setCancelable(false)
                .create()
            //alert.show()*/

            launchActivity<SplashScreen> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }
}