package com.feedbacktower.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridDecorator(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean = true) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos: Int = parent.getChildAdapterPosition(view)
        val column: Int = pos.rem(spanCount)
        if (includeEdge) {
            outRect.left = spacing - column.times(spacing / spanCount)
            outRect.right = (column + 1) * spacing / spanCount
            outRect.top = spacing
        } else {
            outRect.left = column.times(spacing / spanCount)
            outRect.right = spacing - (column + 1) * spacing / spanCount
            outRect.top = spacing
        }
    }
}