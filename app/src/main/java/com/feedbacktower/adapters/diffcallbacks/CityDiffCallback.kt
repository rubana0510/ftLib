package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.models.City

class CityDiffCallback : DiffUtil.ItemCallback<City>() {
    override fun areItemsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: City, newItem: City): Boolean {
        return oldItem == newItem
    }

}
