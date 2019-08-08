package com.feedbacktower.callbacks

import androidx.viewpager.widget.ViewPager

class OnPageChangeListener(private val pageSelected: (pos: Int) -> Unit): ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        pageSelected(position)
    }

}