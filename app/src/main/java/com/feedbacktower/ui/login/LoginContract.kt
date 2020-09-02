package com.feedbacktower.ui.login

import com.feedbacktower.data.models.User
import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView

interface LoginContract {
    interface View: BaseView{
        fun onLoginSuccess(user: User)
        fun showLoginProgress()
        fun hideLoginProgress()
    }

    interface Presenter: BasePresenter<View>{
        fun login(phone: String, password: String)
    }
}