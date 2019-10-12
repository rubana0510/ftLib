package com.feedbacktower.ui.reviews.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.MyReviewListAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentMyReviewsBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast


class MyReviewsFragment : BaseViewFragmentImpl(), MyReviewsContract.View {
    private lateinit var reviewListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var reviewAdapter: MyReviewListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
    private var list: ArrayList<Review> = ArrayList()
    private var listOver = false
    private var fetching = false
    private lateinit var presenter: MyReviewsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMyReviewsBinding.inflate(inflater, container, false)
        presenter = MyReviewsPresenter()
        presenter.attachView(this)
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
        presenter.fetchReviews(timestamp, initial)
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
