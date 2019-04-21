package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.CountDiffCallback
import com.feedbacktower.data.local.models.Count
import com.feedbacktower.databinding.ItemCountViewBinding
/**
 * Created by sanket on 16-04-2019.
 */
class CountAdapter : ListAdapter<Count, CountAdapter.ViewHolder>(CountDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCountViewBinding.inflate(
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

    private fun createClickListener(item: Count): View.OnClickListener = View.OnClickListener {

    }


    fun getItemAtPos(position: Int): Count = getItem(position)

    class ViewHolder(
        val binding: ItemCountViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Count) {
            binding.apply {
                count = item
                executePendingBindings()
            }
        }

    }
}