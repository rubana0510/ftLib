package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.PlanDiffCallback
import com.feedbacktower.data.models.SubscriptionPlan
import com.feedbacktower.databinding.ItemPlanBinding

/**
 * Created by sanket on 16-04-2019.
 */
class PlanAdapter(val onItemClick: (plan: SubscriptionPlan) -> Unit) :
    ListAdapter<SubscriptionPlan, PlanAdapter.ViewHolder>(PlanDiffCallback()) {
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
        holder.bind(createClickListener(item), item)
    }

    private fun createClickListener(item: SubscriptionPlan): View.OnClickListener = View.OnClickListener {
        onItemClick(item)
    }


    fun getItemAtPos(position: Int): SubscriptionPlan = getItem(position)

    class ViewHolder(
        val binding: ItemPlanBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: SubscriptionPlan) {
            binding.apply {
                clickListener = listener
                plan = item
                executePendingBindings()
            }
        }

    }
}