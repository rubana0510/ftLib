package com.feedbacktower.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.feedbacktower.adapters.CategoryListAdapter
import com.feedbacktower.databinding.FragmentSelectCityBinding
import com.feedbacktower.util.InjectorUtils
import com.feedbacktower.viewmodels.BusinessCategoriesViewModel

class SelectCityFragment : Fragment() {

    private lateinit var viewModel: BusinessCategoriesViewModel

    companion object {
        fun getInstance(): SelectCityFragment {
            val fragment = SelectCityFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentSelectCityBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root
        val factory = InjectorUtils.provideBusinessCategoriesViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(BusinessCategoriesViewModel::class.java)
        val adapter = CategoryListAdapter()
        binding.categoryGridView.adapter = adapter
        subscribeUi(adapter)
        return binding.root
    }

    private fun subscribeUi(adapter: CategoryListAdapter) {
        viewModel.fetchBusinessCategories()
        viewModel.categoriesLiveData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}
