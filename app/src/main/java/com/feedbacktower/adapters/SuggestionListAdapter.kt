package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.SuggestionDiffCallback
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.ItemSuggestionBinding

/**
 * Created by sanket on 16-04-2019.
 */
class SuggestionListAdapter : ListAdapter<Suggestion, SuggestionListAdapter.ViewHolder>(SuggestionDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSuggestionBinding.inflate(
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

    private fun createClickListener(item: Suggestion): View.OnClickListener = View.OnClickListener {

    }

    fun getItemAtPos(position: Int): Suggestion = getItem(position)

    class ViewHolder(
        val binding: ItemSuggestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Suggestion) {
            binding.apply {
                suggestion = item
                replyClickListener = listener
                executePendingBindings()
            }
        }
    }
}