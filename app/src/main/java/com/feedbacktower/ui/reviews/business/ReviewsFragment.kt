package com.feedbacktower.ui.reviews.business

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedbacktower.adapters.ReviewListAdapter
import com.feedbacktower.util.callbacks.ScrollListener
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentReviewsBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast


class ReviewsFragment : BaseViewFragmentImpl(), ReviewsContract.View {
    private lateinit var binding: FragmentReviewsBinding
    private lateinit var reviewAdapter: ReviewListAdapter
    private lateinit var businessId: String
    private var list: ArrayList<Review> = ArrayList()
    private var listOver = false
    private var fetching = false
    private lateinit var presenter: ReviewsPresenter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentReviewsBinding.inflate(inflater, container, false)
        presenter = ReviewsPresenter()
        presenter.attachView(this)
        val args: ReviewsFragmentArgs? by navArgs()
        businessId = args?.businessId ?: throw IllegalArgumentException("Invalid business")
        if (args?.businessId == null || args?.businessId.equals("0")) {
            businessId = AppPrefs.getInstance(requireContext()).user?.business?.id
                ?: throw IllegalArgumentException("Invalid business")
            Log.d("ReviewsFragment: ", "businessId: $businessId")
        }
        initUI()
        return binding.root
    }

    private fun initUI() {
        //setup list
        val layoutManager = LinearLayoutManager(context)
        binding.reviewListView.layoutManager = layoutManager
        binding.reviewListView.itemAnimator = DefaultItemAnimator()
        binding.reviewListView.setHasFixedSize(true)
        reviewAdapter = ReviewListAdapter(list)
        binding.reviewListView.adapter = reviewAdapter

        binding.reviewListView.addOnScrollListener(ScrollListener {
            if (listOver) return@ScrollListener

            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchReviews(item.createdAt)
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            fetchReviews(initial = true)
        }
        fetchReviews(initial = true)
    }


    override fun showProgress() {
        super.showProgress()
        binding.swipeRefresh.isRefreshing = true
        fetching = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        binding.swipeRefresh.isRefreshing = false
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
            if (initial) {
                list.clear()
                binding.isListEmpty = it.isEmpty()
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
