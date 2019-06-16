package com.feedbacktower.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.adapters.CategoryInterestAdapter
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.databinding.FragmentSelectInterestsBinding
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.setGrid
import org.jetbrains.anko.toast

class SelectInterestsFragment : Fragment() {
    private lateinit var adapter: CategoryInterestAdapter

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
        val binding = FragmentSelectInterestsBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root
        adapter = CategoryInterestAdapter(toggleListener)
        binding.categoryGridView.setGrid(requireContext(), 2)
        binding.categoryGridView.adapter = adapter
        binding.saveSelectionButton.setOnClickListener { navigateNext() }
        getInterests(binding)
        return binding.root
    }

    private fun navigateNext() {
        SelectInterestsFragmentDirections.actionSelectInterestsFragmentToAccountTypeSelectionFragment().let {
            findNavController().navigate(it)
        }
    }

    private val toggleListener = object : CategoryInterestAdapter.ToggleListener {
        override fun categoryToggled(item: BusinessCategory) {
            toggleCategoryInterest(item)
        }
    }

    private fun toggleCategoryInterest(item: BusinessCategory) {
        ProfileManager.getInstance()
            .setUnsetCategoryInterest(item.id, item.selected) { CategoriesResponse, error ->
                if (error != null) {
                    //requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@setUnsetCategoryInterest
                }
            }
    }

    private fun getInterests(binding: FragmentSelectInterestsBinding) {
        binding.isLoading = true
        ProfileManager.getInstance()
            .getFetauredCategories { CategoriesResponse, error ->
                binding.isLoading = false
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@getFetauredCategories
                }
                adapter.submitList(CategoriesResponse?.featured)
            }
    }
}
