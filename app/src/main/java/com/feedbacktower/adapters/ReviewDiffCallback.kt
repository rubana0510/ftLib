package com.feedbacktower.adapters

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.models.Review

class ReviewDiffCallback: DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.reviewId ==  newItem.reviewId
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem ==  newItem
    }

}
