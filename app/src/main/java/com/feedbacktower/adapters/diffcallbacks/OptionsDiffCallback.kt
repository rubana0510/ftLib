package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.local.models.AccountOption

class OptionsDiffCallback : DiffUtil.ItemCallback<AccountOption>() {
    override fun areItemsTheSame(oldItem: AccountOption, newItem: AccountOption): Boolean {
        return oldItem.summary == newItem.summary
    }

    override fun areContentsTheSame(oldItem: AccountOption, newItem: AccountOption): Boolean {
        return oldItem == newItem
    }

}
