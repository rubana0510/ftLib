package com.feedbacktower.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.feedbacktower.adapters.CategoryListAdapter
import com.feedbacktower.databinding.DialogSelectInterestsBinding
import com.feedbacktower.util.InjectorUtils
import com.feedbacktower.viewmodels.BusinessCategoriesViewModel

class SelectInterestsFragment: DialogFragment() {

    private lateinit var viewModel: BusinessCategoriesViewModel
    companion object {
        fun getInstance(): SelectInterestsFragment {
            val fragment = SelectInterestsFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DialogSelectInterestsBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root
        val factory = InjectorUtils.provideBusinessCategoriesViewModelFactory(context)
        viewModel = ViewModelProviders.of(this, factory).get(BusinessCategoriesViewModel::class.java)
        val adapter = CategoryListAdapter()
        binding.categoryGridView.adapter = adapter
        subscribeUi(adapter)
        return binding.root
    }

    private fun subscribeUi(adapter: CategoryListAdapter) {
        viewModel.getCategories().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}
