package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.OptionsDiffCallback
import com.feedbacktower.data.local.models.AccountOption
import com.feedbacktower.databinding.ItemAccountOptionBinding

/**
 * Created by sanket on 16-04-2019.
 */
class AccountOptionsAdapter(val onItemClick: (AccountOption) -> Unit) :
    ListAdapter<AccountOption, AccountOptionsAdapter.ViewHolder>(OptionsDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAccountOptionBinding.inflate(
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

    private fun createClickListener(item: AccountOption): View.OnClickListener = View.OnClickListener {
        onItemClick(item)
    }


    fun getItemAtPos(position: Int): AccountOption = getItem(position)

    class ViewHolder(
        val binding: ItemAccountOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: AccountOption) {
            binding.apply {
                onItemClickListener = listener
                option = item
                executePendingBindings()
            }
        }

    }
}