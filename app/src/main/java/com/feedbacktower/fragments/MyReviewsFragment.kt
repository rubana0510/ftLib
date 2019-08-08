package com.feedbacktower.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.R
import com.feedbacktower.adapters.MyReviewListAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentMyReviewsBinding
import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.util.Constants
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast


class MyReviewsFragment : Fragment() {
    private lateinit var reviewListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var reviewAdapter: MyReviewListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
    private var list: ArrayList<Review> = ArrayList()
    private var listOver = false
    private var fetching = false
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

        val layoutManager = LinearLayoutManager(context)
        reviewListView.layoutManager = layoutManager
        reviewListView.itemAnimator = DefaultItemAnimator()
        reviewListView.setHasFixedSize(true)
        reviewAdapter = MyReviewListAdapter(list)
        reviewListView.addOnScrollListener(ScrollListener {
            if (listOver || list.size == 0) return@ScrollListener

            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchReviews(item.createdAt)
                }
            }
        })
        reviewListView.adapter = reviewAdapter

        swipeRefresh.setOnRefreshListener {
            fetchReviews(initial = true)
        }
        fetchReviews(initial = true)
    }

    private fun fetchReviews(timestamp: String = "", initial: Boolean = false) {
        if (fetching) return

        swipeRefresh.isRefreshing = true
        fetching = true
        ReviewsManager.getInstance()
            .getMyReviews(timestamp) { response, error ->
                if (error != null) {
                    requireContext().toast("Could not get reviews")
                    return@getMyReviews
                }
                swipeRefresh.isRefreshing = false
                fetching = false
                response?.review?.let {
                    listOver = it.size < Constants.PAGE_SIZE
                    if (initial) {
                        isListEmpty = it.isEmpty()
                        list.clear()
                    }
                    list.addAll(it)
                    reviewAdapter.notifyDataSetChanged()
                }
            }
    }
}
