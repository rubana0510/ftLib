package com.feedbacktower.fragments.business


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.SuggestionListAdapter
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.FragmentSuggestionsBinding


class SuggestionsFragment : Fragment() {
    private lateinit var suggestionListView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var suggestionAdapter: SuggestionListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSuggestionsBinding.inflate(inflater, container, false)
        initUI(binding)
        return binding.root
    }

    private fun initUI(binding: FragmentSuggestionsBinding) {
        suggestionListView = binding.suggestionListView
        swipeRefresh = binding.swipeRefresh
        message = binding.message

        //setup list
        suggestionListView.layoutManager = LinearLayoutManager(requireContext())
        suggestionListView.itemAnimator = DefaultItemAnimator()
        suggestionAdapter = SuggestionListAdapter()
        suggestionListView.adapter = suggestionAdapter
        fetchSuggestionList()
    }


    private fun fetchSuggestionList() {
        val list = listOf(
            Suggestion("1", "1", "Sanket Naik", "2", "Please change1", "5", "USER", "", ""),
            Suggestion("2", "1", "Sanket Naik", "2", "Please change2", "4", "USER", "", ""),
            Suggestion("3", "1", "Sanket Naik", "2", "Please change3", "2", "USER", "", "")
        )
        suggestionAdapter.submitList(list)
    }


}
