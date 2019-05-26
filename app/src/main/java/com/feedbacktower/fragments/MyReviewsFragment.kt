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
import com.feedbacktower.R
import com.feedbacktower.adapters.MyReviewListAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentMyReviewsBinding
import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast


class MyReviewsFragment : Fragment() {
    private lateinit var reviewListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var reviewAdapter: MyReviewListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMyReviewsBinding.inflate(inflater, container, false)
        initUI(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initUI(binding: FragmentMyReviewsBinding) {

        reviewListView = binding.reviewListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        isListEmpty = binding.isListEmpty
        isLoading = binding.isLoading

        //setup list
        reviewListView.setVertical(requireContext())
        reviewAdapter = MyReviewListAdapter()
        reviewListView.adapter = reviewAdapter

        swipeRefresh.setOnRefreshListener {
            fetchReview()
        }
        fetchReview()
    }

    private fun fetchReview(timestamp: String = "") {
        swipeRefresh.isRefreshing = true
        ReviewsManager.getInstance()
            .getMyReviews(timestamp) { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@getMyReviews
                }
                swipeRefresh.isRefreshing = false
                isListEmpty = response?.review?.isEmpty()
                reviewAdapter.submitList(response?.review)
            }
    }
}
