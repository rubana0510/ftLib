package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.SearchBusinessDiffCallback
import com.feedbacktower.databinding.ItemSearchBusinessBinding
import com.feedbacktower.network.models.SearchBusiness

/**
 * Created by sanket on 16-04-2019.
 */
class SearchBusinessAdapter(val listener: Listener) :
    ListAdapter<SearchBusiness, SearchBusinessAdapter.ViewHolder>(SearchBusinessDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSearchBusinessBinding.inflate(
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

    private fun createClickListener(item: SearchBusiness): View.OnClickListener = View.OnClickListener {
        listener.onItemClick(item)
    }


    fun getItemAtPos(position: Int): SearchBusiness = getItem(position)

    class ViewHolder(
        val binding: ItemSearchBusinessBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: SearchBusiness) {
            binding.apply {
                onItemClick = listener
                business = item
                executePendingBindings()
            }
        }

    }

    interface Listener {
        fun onItemClick(item: SearchBusiness)
    }
}