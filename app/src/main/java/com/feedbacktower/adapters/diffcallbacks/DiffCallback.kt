package com.feedbacktower.adapters.diffcallbacks

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.models.BaseModel

class DiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem is BaseModel && newItem is BaseModel) {
            return oldItem.modelId == newItem.modelId
        }
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

}
