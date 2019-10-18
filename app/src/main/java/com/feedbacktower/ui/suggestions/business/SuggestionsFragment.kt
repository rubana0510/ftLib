package com.feedbacktower.ui.suggestions.business

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.feedbacktower.adapters.SuggestionListAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.FragmentSuggestionsBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetSuggestionsResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.ui.suggestions.business.reply.ReplySuggestionDialog
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast


class SuggestionsFragment : BaseViewFragmentImpl(), SuggestionsContract.View {
    private lateinit var binding: FragmentSuggestionsBinding
    private lateinit var presenter: SuggestionsPresenter
    private lateinit var suggestionAdapter: SuggestionListAdapter
    private val list: ArrayList<Suggestion> = ArrayList()
    private var fetching = false
    private var listOver = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuggestionsBinding.inflate(inflater, container, false)
        presenter = SuggestionsPresenter()
        presenter.attachView(this)
        (activity as AppCompatActivity).supportActionBar?.show()
        initUI()
        return binding.root
    }

    private fun initUI() {
        //setup list
        val layoutManager = LinearLayoutManager(context)
        binding.suggestionListView.layoutManager = layoutManager
        binding.suggestionListView.itemAnimator = DefaultItemAnimator()
        binding.suggestionListView.setHasFixedSize(true)
        suggestionAdapter = SuggestionListAdapter(list, onReplyClick)
        binding.suggestionListView.adapter = suggestionAdapter

        binding.suggestionListView.addOnScrollListener(ScrollListener {
            if (listOver) return@ScrollListener

            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchSuggestionList(item.createdAt)
                }
            }
        })
        binding.swipeRefresh.setOnRefreshListener {
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

    override fun onFetched(response: GetSuggestionsResponse?, initial: Boolean) {
        response?.suggestions?.let {
            listOver = it.size < Constants.PAGE_SIZE
            if (initial) {
                list.clear()
                binding.isListEmpty = it.isEmpty()
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
