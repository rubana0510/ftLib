package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.network.models.SearchBusiness

class SearchBusinessDiffCallback : DiffUtil.ItemCallback<SearchBusiness>() {
    override fun areItemsTheSame(oldItem: SearchBusiness, newItem: SearchBusiness): Boolean {
        return oldItem.businessId == newItem.businessId
    }

    override fun areContentsTheSame(oldItem: SearchBusiness, newItem: SearchBusiness): Boolean {
        return oldItem == newItem
    }

}
