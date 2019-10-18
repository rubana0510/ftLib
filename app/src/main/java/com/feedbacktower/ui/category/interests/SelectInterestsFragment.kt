package com.feedbacktower.ui.category.interests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.feedbacktower.adapters.CategoryInterestAdapter
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.databinding.FragmentSelectInterestsBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetCategoriesResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.setGrid
import org.jetbrains.anko.toast

class SelectInterestsFragment : BaseViewFragmentImpl(), InterestsContract.View {

    private lateinit var binding: FragmentSelectInterestsBinding
    private lateinit var presenter: InterestsPresenter
    private lateinit var adapter: CategoryInterestAdapter
    private var list: ArrayList<BusinessCategory> = ArrayList()

    companion object {
        fun getInstance(): SelectInterestsFragment = SelectInterestsFragment().apply { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectInterestsBinding.inflate(inflater, container, false)
        presenter = InterestsPresenter()
        presenter.attachView(this)
        adapter = CategoryInterestAdapter(list, toggleListener)
        binding.categoryGridView.setGrid(requireContext(), 2)
        binding.categoryGridView.adapter = adapter
        binding.saveSelectionButton.setOnClickListener { navigateNext() }
        presenter.fetch()
        return binding.root
    }

    private fun navigateNext() {
        SelectInterestsFragmentDirections.actionSelectInterestsFragmentToAccountTypeSelectionFragment()
            .let {
                findNavController().navigate(it)
            }
    }

    private val toggleListener = object : CategoryInterestAdapter.ToggleListener {
        override fun categoryToggled(item: BusinessCategory) {
            presenter.toggleInterest(item)
        }
    }

    override fun showProgress() {
        super.showProgress()
        binding.isLoading = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        binding.isLoading = false
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }

    override fun onFetched(response: GetCategoriesResponse?) {
        response?.featured?.let {
            list.clear()
            list.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onToggled() {

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyView()
    }
}
