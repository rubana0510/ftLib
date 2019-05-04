package com.feedbacktower.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.adapters.CategoryListAdapter
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.databinding.FragmentSelectInterestsBinding
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.setGrid
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast

class SelectCategoryFragment : Fragment() {
    private lateinit var adapter: CategoryListAdapter

    companion object {
        fun getInstance(): SelectCategoryFragment {
            val fragment = SelectCategoryFragment()
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
        adapter = CategoryListAdapter(toggleListener)
        binding.categoryGridView.setVertical(requireContext())
        binding.categoryGridView.adapter = adapter
        getInterests(binding)
        return binding.root
    }


    private val toggleListener = object : CategoryListAdapter.ToggleListener {
        override fun categoryToggled(item: BusinessCategory) {
            findNavController().navigateUp()
        }
    }

    private fun getInterests(binding: FragmentSelectInterestsBinding) {
        binding.isLoading = true
        ProfileManager.getInstance()
            .getAllCategories { CategoriesResponse, error ->
                binding.isLoading = false
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@getAllCategories
                }
                adapter.submitList(CategoriesResponse?.featured)
            }
    }
}
