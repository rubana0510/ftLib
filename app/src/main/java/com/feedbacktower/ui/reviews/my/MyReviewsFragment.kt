package com.feedbacktower.ui.reviews.my

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedbacktower.App
import com.feedbacktower.adapters.MyReviewListAdapter
import com.feedbacktower.util.callbacks.ScrollListener
import com.feedbacktower.data.models.Review
import com.feedbacktower.databinding.FragmentMyReviewsBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetReviewsResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast
import javax.inject.Inject


class MyReviewsFragment : BaseViewFragmentImpl(), MyReviewsContract.View {
    private lateinit var binding: FragmentMyReviewsBinding
    private lateinit var adapter: MyReviewListAdapter
    private var list: ArrayList<Review> = ArrayList()
    private var listOver = false
    private var fetching = false
    @Inject
    lateinit var presenter: MyReviewsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (requireActivity().applicationContext as App).appComponent.reviewComponent().create().inject(this)
        binding = FragmentMyReviewsBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        initUI()
        return binding.root
    }

    private fun initUI() {
        //setup list

        val layoutManager = LinearLayoutManager(context)
        binding.reviewListView.layoutManager = layoutManager
        binding.reviewListView.itemAnimator = DefaultItemAnimator()
        binding.reviewListView.setHasFixedSize(true)
        adapter = MyReviewListAdapter(list)
        binding.reviewListView.addOnScrollListener(ScrollListener {
            if (listOver || list.size == 0) return@ScrollListener

            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchReviews(item.createdAt)
                }
            }
        })
        binding.reviewListView.adapter = adapter

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
        presenter.fetchReviews(timestamp, initial)
    }

    override fun onReviewsFetched(response: GetReviewsResponse?, initial: Boolean) {
        response?.review?.let {
            listOver = it.size < Constants.PAGE_SIZE
            if (initial) {
                list.clear()
                binding.isListEmpty = it.isEmpty()
            }
            list.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
