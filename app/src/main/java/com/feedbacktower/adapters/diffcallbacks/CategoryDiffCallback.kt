package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.models.BusinessCategory

class CategoryDiffCallback : DiffUtil.ItemCallback<BusinessCategory>() {
    override fun areItemsTheSame(oldItem: BusinessCategory, newItem: BusinessCategory): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BusinessCategory, newItem: BusinessCategory): Boolean {
        return oldItem == newItem
    }

}
