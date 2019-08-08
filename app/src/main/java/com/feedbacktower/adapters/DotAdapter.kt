package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.databinding.ItemViewpagerDotBinding

/**
 * Created by sanket on 16-04-2019.
 */
class DotAdapter(private val dotList: List<Boolean>, private val onDotClick: (pos: Int) -> Unit) :
    RecyclerView.Adapter<DotAdapter.ViewHolder>() {
    override fun getItemCount(): Int = dotList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemViewpagerDotBinding.inflate(
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

    private fun getItem(position: Int): Boolean = dotList[position]

    private fun createClickListener(selected: Boolean, position: Int): View.OnClickListener = View.OnClickListener {
        onDotClick(position)
    }


    fun getItemAtPos(position: Int): Boolean = getItem(position)

    class ViewHolder(
        val binding: ItemViewpagerDotBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Boolean) {
            binding.apply {
                clickListener = listener
                selected = item
                image.isSelected = item
                executePendingBindings()
            }
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<Boolean>() {
        override fun areItemsTheSame(oldItem: Boolean, newItem: Boolean): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Boolean, newItem: Boolean): Boolean {
            return oldItem == newItem
        }

    }
}