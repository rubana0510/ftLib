package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.data.models.SubscriptionPlan

class PlanDiffCallback : DiffUtil.ItemCallback<SubscriptionPlan>() {
    override fun areItemsTheSame(oldItem: SubscriptionPlan, newItem: SubscriptionPlan): Boolean {
        return oldItem.planId == newItem.planId
    }

    override fun areContentsTheSame(oldItem: SubscriptionPlan, newItem: SubscriptionPlan): Boolean {
        return oldItem == newItem
    }

}
