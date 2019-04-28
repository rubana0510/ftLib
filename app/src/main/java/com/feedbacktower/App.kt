package com.feedbacktower

import android.app.Application
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.network.utils.ConnectivityReceiver


class App : Application() {

    companion object {
        private var mInstance: App? = null
        @Synchronized
        fun getInstance(): App {
            return mInstance!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
    }

    fun setConnectivityListener(listener: ConnectivityReceiver.ConnectivityReceiverListener) {
        ConnectivityReceiver.connectivityReceiverListener = listener
    }

    fun getToken(): String? {
        return AppPrefs.getInstance(this).authToken
    }
}