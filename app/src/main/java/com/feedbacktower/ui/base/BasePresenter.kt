package com.feedbacktower.ui.base;

interface BasePresenter<in V : BaseView> {
    fun attachView(view: V)
    fun destroyView()
}
