package com.feedbacktower.ui.base

open class BasePresenterImpl<V : BaseView> : BasePresenter<V> {
    protected var view: V? = null
        private set

    override fun attachView(view: V) {
        this.view = view
    }

    override fun destroyView() {
        this.view = null
    }
}