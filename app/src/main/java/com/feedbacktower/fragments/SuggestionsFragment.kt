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
import com.feedbacktower.databinding.FragmentSuggestionsBinding
import com.feedbacktower.network.manager.SuggestionsManager


class SuggestionsFragment : Fragment() {
    private lateinit var suggestionListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var suggestionAdapter: SuggestionListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
    private var mySuggestions: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSuggestionsBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        val args: SuggestionsFragmentArgs by navArgs()
        mySuggestions = args.mySuggestions
        initUI(binding)
        return binding.root
    }

    private fun initUI(binding: FragmentSuggestionsBinding) {
        binding.toolbar.title = if (mySuggestions) "My Suggestions" else "Suggestions"

        suggestionListView = binding.suggestionListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message
        isListEmpty = binding.isListEmpty
        isLoading = binding.isLoading

        //setup list
        suggestionListView.layoutManager = LinearLayoutManager(requireContext())
        suggestionListView.itemAnimator = DefaultItemAnimator()
        suggestionAdapter = SuggestionListAdapter()
        suggestionListView.adapter = suggestionAdapter
        swipeRefresh.setOnRefreshListener {
            fetchSuggestionList()
        }
        fetchSuggestionList()
    }

    private fun fetchSuggestionList() {
        swipeRefresh.isRefreshing = true
        SuggestionsManager.getInstance()
            .getSuggestions("") { response, error ->
                swipeRefresh.isRefreshing = false
                isListEmpty = response?.suggestions?.isEmpty()
                suggestionAdapter.submitList(response?.suggestions)
            }
    }
}