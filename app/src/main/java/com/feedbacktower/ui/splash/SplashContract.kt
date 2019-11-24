package com.feedbacktower.ui.splash

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface SplashContract {
    interface Presenter: BasePresenter<View>{
        fun refreshToken()
    }

    interface View: BaseView{

    }
}
