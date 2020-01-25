package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.ItemSuggestionBinding
import com.feedbacktower.ui.suggestions.my.MySuggestionsFragmentDirections

/**
 * Created by sanket on 16-04-2019.
 */
class MySuggestionListAdapter(private val list: List<Suggestion>, private val listener: ReplyListener?) :
    RecyclerView.Adapter<MySuggestionListAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

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
        holder.bind(createClickListener(item), createProfileClickListener(item), item)
    }

    private fun getItem(position: Int): Suggestion {
        return list[position]
    }

    private fun createClickListener(item: Suggestion): View.OnClickListener = View.OnClickListener {
        listener?.onReplyClick(item)
    }

    private fun createProfileClickListener(item: Suggestion): View.OnClickListener = View.OnClickListener { view ->
        MySuggestionsFragmentDirections.actionMySuggestionsFragmentToNavigationBusinessDetail(item.businessId).let {
            view.findNavController().navigate(it)
        }
    }

    fun updateItem(item: Suggestion) {

    }

    fun getItemAtPos(position: Int): Suggestion = getItem(position)

    class ViewHolder(
        val binding: ItemSuggestionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, profileListener: View.OnClickListener, item: Suggestion) {
            binding.apply {
                suggestion = item
                hideReplyButton = true
                openProfileListener = profileListener
                replyClickListener = listener
                executePendingBindings()
            }
        }
    }

    interface ReplyListener {
        fun onReplyClick(suggestion: Suggestion)
    }
}