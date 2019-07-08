package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.CategoryDiffCallback
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.databinding.ItemListCategoryBinding

/**
 * Created by sanket on 12-02-2019.
 */
class CategoryListAdapter(
    private val list: List<BusinessCategory>,
    private val listener: ToggleListener
) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    override fun getItemCount(): Int = list.size

    enum class Type { LIST, GRID }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ItemListCategoryBinding.inflate(
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

    private fun getItem(position: Int): BusinessCategory {
        return list[position]
    }

    private fun createClickListener(item: BusinessCategory, position: Int): View.OnClickListener =
        View.OnClickListener {
            item.selected = !item.selected
            notifyItemChanged(position)
            listener.categoryToggled(item)
        }

    fun getItemAtPos(position: Int): BusinessCategory = getItem(position)

    class ViewHolder(
        val binding: ItemListCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: BusinessCategory) {
            binding.apply {
                businessCategory = item
                clickListener = listener
                executePendingBindings()
            }
        }
    }

    public interface ToggleListener {
        fun categoryToggled(item: BusinessCategory)
    }
}