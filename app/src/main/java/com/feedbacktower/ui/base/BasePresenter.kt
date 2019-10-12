package com.feedbacktower.ui.base;

interface BasePresenter<in V : BaseView> {
    fun attachView(v: V)
    fun destroyView()
}
