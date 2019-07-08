package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.ItemReviewBinding
import com.feedbacktower.fragments.ReviewsFragmentDirections

/**
 * Created by sanket on 16-04-2019.
 */
class ReviewListAdapter(val list: List<Review>) : RecyclerView.Adapter<ReviewListAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.bind(createProfileClickListener(item), item)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private fun createProfileClickListener(item: Review): View.OnClickListener = View.OnClickListener { view ->
        val businessId = item.user?.business?.id ?: return@OnClickListener
        ReviewsFragmentDirections.actionNavigationReviewToBusinessDetailsActivity(businessId).let {
            view.findNavController().navigate(it)
        }
    }

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