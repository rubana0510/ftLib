package com.feedbacktower

import android.app.Application
import com.feedbacktower.di.DaggerAppComponent

class App : Application() {
val appComponent = DaggerAppComponent.factory().create(this)

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
}