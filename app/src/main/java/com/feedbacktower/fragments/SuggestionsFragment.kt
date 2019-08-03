package com.feedbacktower.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.SuggestionListAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.FragmentSuggestionsBinding
import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.util.Constants


class SuggestionsFragment : Fragment() {
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
            dialog.show(fragmentManager, ReplySuggestionDialog.TAG)

        }
    }

    private fun fetchSuggestionList(timestamp: String = "", initial: Boolean = false) {
        if(fetching) return
        swipeRefresh.isRefreshing = true
        fetching = true
        SuggestionsManager.getInstance()
            .getSuggestions(timestamp) { response, error ->
                swipeRefresh.isRefreshing = false
                fetching = false
                response?.suggestions?.let {
                    listOver = it.size < Constants.PAGE_SIZE
                    isListEmpty = it.isEmpty()
                    if(initial){
                        list.clear()
                    }
                    list.addAll(it)
                    suggestionAdapter.notifyDataSetChanged()
                }
            }
    }
}
