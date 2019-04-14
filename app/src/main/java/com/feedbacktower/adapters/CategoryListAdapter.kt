package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.databinding.ItemInterestsCategoryBinding

/**
 * Created by sanket on 12-02-2019.
 */
class CategoryListAdapter : ListAdapter<BusinessCategory, CategoryListAdapter.ViewHolder>(CategoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemInterestsCategoryBinding.inflate(
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

    private fun createClickListener(item: BusinessCategory): View.OnClickListener = View.OnClickListener {
        item.catSelected = !item.catSelected
    }

    fun getItemAtPos(position: Int): BusinessCategory = getItem(position)

    class ViewHolder(
        val binding: ItemInterestsCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: BusinessCategory) {
            binding.apply {
                businessCategory = item
                clickListener = listener
                executePendingBindings()
            }
        }
    }
}