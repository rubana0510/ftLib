package com.feedbacktower.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(
        listener: View.OnClickListener,
        videoClickListener: View.OnClickListener?,
        profileListener: View.OnClickListener,
        item: T
    )
}