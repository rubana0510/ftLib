package com.feedbacktower.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentReviewsBinding
import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.util.setVertical
import java.lang.IllegalArgumentException


class ReviewsFragment : Fragment() {
    private lateinit var reviewListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var reviewAdapter: ReviewListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
    private lateinit var businessId: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentReviewsBinding.inflate(inflater, container, false)
        val args: ReviewsFragmentArgs? by navArgs()
        businessId = args?.businessId ?: throw IllegalArgumentException("Invalid business")
        if (args?.businessId == null || args?.businessId.equals("0")) {
            businessId = AppPrefs.getInstance(requireContext()).user?.business?.id
                ?: throw IllegalArgumentException("Invalid business")
            Log.d("ReviewsFragment: ", "businessId: $businessId")
        }
        initUI(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initUI(binding: FragmentReviewsBinding) {
      //  binding.toolbar.title = "Reviews"
        reviewListView = binding.reviewListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        isListEmpty = binding.isListEmpty
        isLoading = binding.isLoading

        //setup list
        reviewListView.setVertical(requireContext())
        reviewAdapter = ReviewListAdapter()
        reviewListView.adapter = reviewAdapter

        swipeRefresh.setOnRefreshListener {
            fetchReview()
        }
        fetchReview()
    }

    private fun fetchReview(timestamp: String = "") {
        swipeRefresh.isRefreshing = true
        ReviewsManager.getInstance()
            .getBusinessReviews(businessId, timestamp) { response, throwable ->
                swipeRefresh.isRefreshing = false
                isListEmpty = response?.review?.isEmpty()
                reviewAdapter.submitList(response?.review)
            }
    }
}
