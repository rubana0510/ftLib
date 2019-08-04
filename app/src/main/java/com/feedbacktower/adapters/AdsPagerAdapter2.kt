package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.data.models.Ad
import com.feedbacktower.databinding.ItemAdPagerBinding

/**
 * Created by sanket on 12-02-2019.
 */
class AdsPagerAdapter2 (
    private  val list: List<Ad>,
    private  val listener: Listener
): RecyclerView.Adapter<AdsPagerAdapter2.ViewHolder>() {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAdPagerBinding.inflate(
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

    private fun getItem(position: Int): Ad {
        return  list[position]
    }

    private fun createClickListener(item: Ad): View.OnClickListener = View.OnClickListener { view ->
        listener.onItemClick(item)
    }

    fun getItemAtPos(position: Int): Ad = getItem(position)

    class ViewHolder(
        val binding: ItemAdPagerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Ad) {
            binding.apply {
                ad = item
                clickListener = listener
                executePendingBindings()
            }
        }
    }

    interface Listener{
        fun onItemClick(ad: Ad)
    }
}