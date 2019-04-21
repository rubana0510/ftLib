package com.feedbacktower.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.SearchBusinessAdapter
import com.feedbacktower.databinding.FragmentSearchBinding
import com.feedbacktower.network.models.SearchBusiness
import com.feedbacktower.util.setVertical


class SearchFragment : Fragment(), SearchBusinessAdapter.Listener {
    private lateinit var searchListView: RecyclerView
    private lateinit var message: TextView
    private lateinit var searchBusinessAdapter: SearchBusinessAdapter
    private var isListEmpty: Boolean? = false
    private var isLoading: Boolean? = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentSearchBinding) {
        searchListView = binding.searchListView
        message = binding.message

        //setup list
        searchListView.setVertical(requireContext())
        searchBusinessAdapter = SearchBusinessAdapter(this)
        searchListView.adapter = searchBusinessAdapter
        isLoading = binding.isLoading
        isListEmpty = binding.isListEmpty
        fetchBusinessList()
    }

    override fun onItemClick(item: SearchBusiness) {
        val direction = SearchFragmentDirections.actionNavigationSearchToBusinessDetailsActivity()
        findNavController().navigate(direction)
    }

    private fun fetchBusinessList() {
        val list = listOf(
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0),
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0),
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0),
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0),
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0),
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0),
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0),
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0),
            SearchBusiness("1", "2", "Magic Muncheez", "Restaurant", "Panaji", "https://via.placeholder.com/150", 5.0)
        )
        isLoading = false
        isListEmpty = list.isEmpty()
        searchBusinessAdapter.submitList(list)
    }


}
