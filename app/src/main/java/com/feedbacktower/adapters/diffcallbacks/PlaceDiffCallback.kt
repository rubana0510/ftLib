package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.models.Place

class PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(
        oldItem: Place,
        newItem: Place
    ): Boolean {
        return oldItem.placeId == newItem.placeId
    }

    override fun areContentsTheSame(
        oldItem: Place,
        newItem: Place
    ): Boolean {
        return  oldItem == newItem
    }

}
