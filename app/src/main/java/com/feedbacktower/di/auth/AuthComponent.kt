package com.feedbacktower.di.auth

import com.feedbacktower.notifications.MyFirebaseMessagingService
import com.feedbacktower.ui.login.LoginScreen
import com.feedbacktower.ui.splash.SplashScreen
import dagger.Subcomponent

@AuthScope
@Subcomponent
interface AuthComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthComponent
    }

    fun inject(activity: LoginScreen)
    fun inject(activity: SplashScreen)
    fun inject(firebaseMessagingService: MyFirebaseMessagingService)
}