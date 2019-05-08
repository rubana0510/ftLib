package com.feedbacktower.fragments


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.models.SearchBusiness
import com.feedbacktower.ui.BusinessDetailsActivity
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.setVertical
import kotlinx.android.synthetic.main.fragment_search.view.*


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
        binding.queryInput.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                search(s)
            }

        })
        //fetchBusinessList()
    }

    private fun search(s: CharSequence?) {
        isLoading = true
        if(s.isNullOrEmpty()) return

        ProfileManager.getInstance()
            .searchBusiness(s.toString()){response, error->
                if(error == null){
                    isLoading = false
                    isListEmpty = response?.businesses?.isEmpty()
                    searchBusinessAdapter.submitList(response?.businesses)
                }
            }
    }

    override fun onItemClick(item: SearchBusiness) {

        requireActivity().launchActivity<BusinessDetailsActivity> {
            putExtra("businessId", item.businessId)
        }
    }

   /* private fun fetchBusinessList() {
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
    }*/


}
