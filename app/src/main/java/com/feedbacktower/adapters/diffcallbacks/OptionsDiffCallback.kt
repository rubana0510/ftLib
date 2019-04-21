package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.local.models.AccountOption

class OptionsDiffCallback : DiffUtil.ItemCallback<AccountOption>() {
    override fun areItemsTheSame(oldItem: AccountOption, newItem: AccountOption): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AccountOption, newItem: AccountOption): Boolean {
        return oldItem == newItem
    }

}
