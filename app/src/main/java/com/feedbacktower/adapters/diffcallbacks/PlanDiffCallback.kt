package com.feedbacktower.adapters.diffcallbacks

import androidx.recyclerview.widget.DiffUtil
import com.feedbacktower.network.models.PlanListResponse

class PlanDiffCallback : DiffUtil.ItemCallback<PlanListResponse.Plan>() {
    override fun areItemsTheSame(oldItem: PlanListResponse.Plan, newItem: PlanListResponse.Plan): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlanListResponse.Plan, newItem: PlanListResponse.Plan): Boolean {
        return oldItem == newItem
    }

}
