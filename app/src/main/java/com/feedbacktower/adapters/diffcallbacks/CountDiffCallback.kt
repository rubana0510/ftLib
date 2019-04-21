package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.local.models.Count

class CountDiffCallback : DiffUtil.ItemCallback<Count>() {
    override fun areItemsTheSame(oldItem: Count, newItem: Count): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Count, newItem: Count): Boolean {
        return oldItem == newItem
    }

}
