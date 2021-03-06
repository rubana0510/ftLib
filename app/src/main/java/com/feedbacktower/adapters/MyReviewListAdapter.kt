package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.DiffCallback
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.ItemReviewBinding
import com.feedbacktower.ui.reviews.my.MyReviewsFragmentDirections

/**
 * Created by sanket on 16-04-2019.
 */
class MyReviewListAdapter(val list: List<Review>) : RecyclerView.Adapter<MyReviewListAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(createProfileClickListener(item), item)
    }

    private fun getItem(position: Int): Review {
        return list[position]
    }

    private fun createProfileClickListener(item: Review): View.OnClickListener = View.OnClickListener { view ->
        MyReviewsFragmentDirections.actionMyReviewsFragmentToNavigationBusinessDetail(item.businessId).let {
            view.findNavController().navigate(it)
        }
    }

    fun getItemAtPos(position: Int): Review = getItem(position)

    class ViewHolder(
        val binding: ItemReviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Review) {
            binding.apply {
                openProfileListener = listener
                review = item
                executePendingBindings()
            }
        }
    }
}