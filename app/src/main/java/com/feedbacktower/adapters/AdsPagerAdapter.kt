package com.feedbacktower.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.feedbacktower.data.models.Ad
import com.feedbacktower.databinding.ItemAdPagerBinding
import kotlin.collections.ArrayList

class AdsPagerAdapter(private val context: Context, private val list: ArrayList<Ad>, private val listener: Listener) :
    PagerAdapter() {
    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemAdPagerBinding.inflate(LayoutInflater.from(context), container, false)
        binding.ad = list[position]
        binding.clickListener = View.OnClickListener { listener.onClick(position) }
        val view = binding.root
        container.addView(view)
        return view
    }

    override fun getCount(): Int = list.size

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as FrameLayout)
    }

    interface Listener {
        fun onClick(pos: Int)
    }
}