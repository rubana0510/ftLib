package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.diffcallbacks.CategoryDiffCallback
import com.feedbacktower.adapters.diffcallbacks.CityDiffCallback
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.data.models.City
import com.feedbacktower.databinding.ItemCityBinding
import com.feedbacktower.databinding.ItemInterestsCategoryBinding
import com.feedbacktower.fragments.SelectCityFragmentDirections

/**
 * Created by sanket on 12-02-2019.
 */
class CityListAdapter(private val list: List<City>, private val listener: Listener) :
    RecyclerView.Adapter<CityListAdapter.ViewHolder>() {

    private var lastCitySelected: City? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(createClickListener(item), item)
    }

    private fun getItem(position: Int): City {
        return list[position]
    }

    private fun createClickListener(item: City): View.OnClickListener = View.OnClickListener { view ->
        listener.onCityClick(item)
    }

    fun getItemAtPos(position: Int): City = getItem(position)

    class ViewHolder(
        val binding: ItemCityBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: City) {
            binding.apply {
                city = item
                clickListener = listener
                executePendingBindings()
            }
        }
    }

    interface Listener {
        fun onCityClick(city: City)
    }
}