package com.feedbacktower.adapters

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.models.Suggestion

class SuggestionDiffCallback: DiffUtil.ItemCallback<Suggestion>() {
    override fun areItemsTheSame(oldItem: Suggestion, newItem: Suggestion): Boolean {
        return oldItem.suggestionId ==  newItem.suggestionId
    }

    override fun areContentsTheSame(oldItem: Suggestion, newItem: Suggestion): Boolean {
        return oldItem ==  newItem
    }

}
