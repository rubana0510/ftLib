package com.feedbacktower.ui.base

open class BasePresenterImpl<V : BaseView> : BasePresenter<V> {
    private var view: V? = null
    override fun attachView(view: V) {
        this.view = view
    }

    override fun destroyView() {
        this.view = null
    }

    protected fun getView(): V? = view
}