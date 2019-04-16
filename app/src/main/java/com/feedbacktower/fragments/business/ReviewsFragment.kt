package com.feedbacktower.fragments.business

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentReviewsBinding


class ReviewsFragment : Fragment() {
    private lateinit var reviewListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var reviewAdapter: ReviewListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentReviewsBinding.inflate(inflater, container, false)
        initUI(binding)
        return binding.root
    }

    private fun initUI(binding: FragmentReviewsBinding) {
        reviewListView = binding.reviewListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message

        //setup list
        reviewListView.layoutManager = LinearLayoutManager(requireContext())
        reviewListView.itemAnimator = DefaultItemAnimator()
        reviewAdapter = ReviewListAdapter()
        reviewListView.adapter = reviewAdapter

        fetchReviewList()
    }

    private fun fetchReviewList() {
        val list = listOf(
            Review("1", "1", "Sanket Naik", "2", "Best Garage1", "5", "Now"),
            Review("2", "1", "Sanket Naik", "2", "Best Garage2", "5", "Now"),
            Review("3", "1", "Sanket Naik", "2", "Best Garage3", "5", "Now")
        )
        reviewAdapter.submitList(list)
    }


}
