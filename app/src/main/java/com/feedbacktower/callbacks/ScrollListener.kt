package com.feedbacktower.callbacks

import androidx.recyclerview.widget.RecyclerView

class ScrollListener(val scrolled: () -> Unit) : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        scrolled()
    }
}