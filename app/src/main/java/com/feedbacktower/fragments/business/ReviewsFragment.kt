package com.feedbacktower.fragments.business

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    private var isListEmpty: Boolean? = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentReviewsBinding.inflate(inflater, container, false)
        initUI(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun initUI(binding: FragmentReviewsBinding) {
        reviewListView = binding.reviewListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        isListEmpty = binding.isListEmpty

        //setup list
        reviewListView.layoutManager = LinearLayoutManager(requireContext())
        reviewListView.itemAnimator = DefaultItemAnimator()
        reviewAdapter = ReviewListAdapter()
        reviewListView.adapter = reviewAdapter

        fetchReviewList()
    }

    private fun fetchReviewList() {
        val list = listOf(
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience using your service. Hope we will get the same for years to come.", "1", "ss"),
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience using your service.", "2", "ss"),
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience.", "5", "ss"),
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience.", "5", "ss"),
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience.", "5", "ss"),
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience.", "5", "ss"),
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience.", "5", "ss"),
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience.", "5", "ss"),
            Review("1", "1", "Sanket Naik", "https://via.placeholder.com/50", "Best Garage1", "It was really great experience.", "5", "ss")
        )
        isListEmpty = list.isEmpty()
        reviewAdapter.submitList(list)
    }


}
