package com.feedbacktower.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.PlanDiffCallback
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.databinding.ItemPlanBinding
import com.feedbacktower.network.models.PlanListResponse

/**
 * Created by sanket on 16-04-2019.
 */
class PlanAdapter : ListAdapter<PlanListResponse.Plan, PlanAdapter.ViewHolder>(PlanDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPlanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(createClickListener(item, position), item)

    }

    private fun createClickListener(item: PlanListResponse.Plan, pos: Int): View.OnClickListener = View.OnClickListener {
        item.isSelected = !item.isSelected
        notifyItemChanged(pos)
    }


    fun getItemAtPos(position: Int): PlanListResponse.Plan = getItem(position)

    class ViewHolder(
        val binding: ItemPlanBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: PlanListResponse.Plan) {
            binding.apply {
                Log.d("PlanAdapter", "Item: $item")
                clickListener = listener
                plan = item
                executePendingBindings()
            }
        }

    }
}