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
import com.feedbacktower.databinding.FragmentMySuggestionsBinding
import com.feedbacktower.network.manager.SuggestionsManager
import org.jetbrains.anko.toast


class MySuggestionsFragment : Fragment() {
    private lateinit var suggestionListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var suggestionAdapter: MySuggestionListAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = true
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
        suggestionListView.layoutManager = LinearLayoutManager(requireContext())
        suggestionListView.itemAnimator = DefaultItemAnimator()
        suggestionAdapter = MySuggestionListAdapter(null)
        suggestionListView.adapter = suggestionAdapter
        swipeRefresh.setOnRefreshListener {
            fetchSuggestionList()
        }
        fetchSuggestionList()
    }

    private fun fetchSuggestionList(timestamp: String = "") {
        swipeRefresh.isRefreshing = true
        SuggestionsManager.getInstance()
            .getMySuggestions(timestamp) { response, error ->
                if(error != null){
                    requireContext().toast(error.message?:getString(R.string.default_err_message))
                    return@getMySuggestions
                }
                swipeRefresh.isRefreshing = false
                isListEmpty = response?.suggestions?.isEmpty()
                suggestionAdapter.submitList(response?.suggestions)
            }
    }
}
