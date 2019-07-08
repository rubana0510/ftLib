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
import com.feedbacktower.R
import com.feedbacktower.adapters.MySuggestionListAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.FragmentMySuggestionsBinding
import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast


class MySuggestionsFragment : Fragment() {
    private lateinit var suggestionListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var suggestionAdapter: MySuggestionListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
    private val list: ArrayList<Suggestion> = ArrayList()
    private var listOver = false
    private var fetching = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentMySuggestionsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        val args: SuggestionsFragmentArgs by navArgs()
        initUI(binding)
        return binding.root
    }

    private fun initUI(binding: FragmentMySuggestionsBinding) {


        suggestionListView = binding.suggestionListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        isListEmpty = binding.isListEmpty
        isLoading = binding.isLoading

        //setup list
        val layoutManager = LinearLayoutManager(requireContext())
        suggestionListView.layoutManager = layoutManager
        suggestionListView.itemAnimator = DefaultItemAnimator()
        suggestionAdapter = MySuggestionListAdapter(list, null)
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

    private fun fetchSuggestionList(timestamp: String = "", initial: Boolean = false) {
        swipeRefresh.isRefreshing = true
        fetching = true
        SuggestionsManager.getInstance()
            .getMySuggestions(timestamp) { response, error ->
                swipeRefresh.isRefreshing = false
                fetching = false
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@getMySuggestions
                }
                response?.suggestions?.let {
                    listOver = it.size < Constants.PAGE_SIZE
                    isListEmpty = it.isEmpty()
                    if (initial)
                        list.clear()
                    list.addAll(it)
                    suggestionAdapter.notifyDataSetChanged()
                }
            }
    }
}
