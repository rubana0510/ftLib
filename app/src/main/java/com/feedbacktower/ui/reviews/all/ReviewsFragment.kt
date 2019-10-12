package com.feedbacktower.ui.reviews.all

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentReviewsBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast
import java.lang.IllegalArgumentException


class ReviewsFragment : BaseViewFragmentImpl(), ReviewsContract.View {

    private lateinit var reviewListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var reviewAdapter: ReviewListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
    private lateinit var businessId: String
    private var list: ArrayList<Review> = ArrayList()
    private var listOver = false
    private var fetching = false
    private lateinit var presenter: ReviewsPresenter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentReviewsBinding.inflate(inflater, container, false)
        presenter = ReviewsPresenter()
        presenter.attachView(this)
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

    private fun initUI(binding: FragmentReviewsBinding) {
        //  binding.toolbar.title = "Reviews"
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
        reviewAdapter = ReviewListAdapter(list)
        reviewListView.adapter = reviewAdapter

        reviewListView.addOnScrollListener(ScrollListener {
            if (listOver) return@ScrollListener

            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchReviews(item.createdAt)
                }
            }
        })

        swipeRefresh.setOnRefreshListener {
            fetchReviews(initial = true)
        }
        fetchReviews(initial = true)
    }

    override fun showProgress() {
        super.showProgress()
        swipeRefresh.isRefreshing = true
        fetching = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        swipeRefresh.isRefreshing = false
        fetching = false
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }

    private fun fetchReviews(timestamp: String = "", initial: Boolean = false) {
        if (fetching) return
        presenter.fetchReviews(businessId, timestamp, initial)
    }

    override fun onReviewsFetched(response: GetReviewsResponse?, initial: Boolean) {
        response?.review?.let {
            listOver = it.size < Constants.PAGE_SIZE
            isListEmpty = it.isEmpty()
            if (initial) {
                list.clear()
            }
            list.addAll(it)
            reviewAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
