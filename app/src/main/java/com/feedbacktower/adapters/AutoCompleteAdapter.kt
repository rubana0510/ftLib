package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.PlaceDiffCallback
import com.feedbacktower.data.models.Place
import com.feedbacktower.databinding.ItemPredictionBinding

/**
 * Created by sanket on 12-02-2019.
 */
class AutoCompleteAdapter (
    private  val list: List<Place>,
    private  val listener: Listener
): RecyclerView.Adapter<AutoCompleteAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPredictionBinding.inflate(
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

    private fun getItem(position: Int): Place {
        return  list[position]
    }

    private fun createClickListener(item: Place): View.OnClickListener = View.OnClickListener { view ->
        listener.onAutoCompleteClick(item)
    }

    fun getItemAtPos(position: Int): Place = getItem(position)

    class ViewHolder(
        val binding: ItemPredictionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Place) {
            binding.apply {
                place = item
                clickListener = listener
                executePendingBindings()
            }
        }
    }

    interface Listener{
        fun onAutoCompleteClick(place: Place)
    }
}