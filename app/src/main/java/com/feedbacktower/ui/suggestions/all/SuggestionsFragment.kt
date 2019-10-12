package com.feedbacktower.ui.suggestions.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.SuggestionListAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.FragmentSuggestionsBinding
import com.feedbacktower.fragments.ReplySuggestionDialog
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetSuggestionsResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast


class SuggestionsFragment : BaseViewFragmentImpl(), SuggestionsContract.View {
    private lateinit var presenter: SuggestionsPresenter
    private lateinit var suggestionListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var suggestionAdapter: SuggestionListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
    private val list: ArrayList<Suggestion> = ArrayList()
    private var fetching = false
    private var listOver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSuggestionsBinding.inflate(inflater, container, false)
        presenter = SuggestionsPresenter()
        presenter.attachView(this)
        (activity as AppCompatActivity).supportActionBar?.show()
        initUI(binding)
        return binding.root
    }

    private fun initUI(binding: FragmentSuggestionsBinding) {
        //binding.toolbar.title = "Suggestions"

        suggestionListView = binding.suggestionListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        isListEmpty = binding.isListEmpty
        isLoading = binding.isLoading

        //setup list
        //setup list
        val layoutManager = LinearLayoutManager(context)
        suggestionListView.layoutManager = layoutManager
        suggestionListView.itemAnimator = DefaultItemAnimator()
        suggestionListView.setHasFixedSize(true)
        suggestionAdapter = SuggestionListAdapter(list, onReplyClick)
        suggestionListView.adapter = suggestionAdapter

        suggestionListView.addOnScrollListener(ScrollListener {
            if (listOver) return@ScrollListener

            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchSuggestionList(item.createdAt)
                }
            }
        })
        swipeRefresh.setOnRefreshListener {
            fetchSuggestionList(initial = true)
        }
        fetchSuggestionList(initial = true)
    }

    private val onReplyClick = object : SuggestionListAdapter.ReplyListener {
        override fun onReplyClick(suggestion: Suggestion) {
            val dialog = ReplySuggestionDialog.getInstance(suggestion)
            dialog.listener = object : ReplySuggestionDialog.ReplyCancelListener {
                override fun onReply(suggestion: Suggestion) {
                    fetchSuggestionList(initial = true)
                }
            }
            dialog.show(fragmentManager!!, ReplySuggestionDialog.TAG)

        }
    }

    private fun fetchSuggestionList(timestamp: String = "", initial: Boolean = false) {
        if (fetching) return
        presenter.fetch(timestamp, initial)
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

    override fun onFetched(response: GetSuggestionsResponse?, initial: Boolean) {
        response?.suggestions?.let {
            listOver = it.size < Constants.PAGE_SIZE
            isListEmpty = it.isEmpty()
            if (initial) {
                list.clear()
            }
            list.addAll(it)
            suggestionAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
