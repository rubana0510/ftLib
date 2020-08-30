package com.feedbacktower.ui.category.interests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.App
import com.feedbacktower.adapters.CategoryInterestAdapter
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.databinding.FragmentSelectInterestsBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetCategoriesResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import com.feedbacktower.util.setGrid
import org.jetbrains.anko.toast
import javax.inject.Inject

class SelectInterestsFragment : BaseViewFragmentImpl(), InterestsContract.View {
    @Inject
    lateinit var presenter: InterestsPresenter
    private lateinit var binding: FragmentSelectInterestsBinding
    private lateinit var adapter: CategoryInterestAdapter
    private var list: ArrayList<BusinessCategory> = ArrayList()
    private var categoriesOver: Boolean = false
    private var categoryDynamicPageSize = Constants.CATEGORY_PAGE_SIZE

    companion object {
        fun getInstance(): SelectInterestsFragment = SelectInterestsFragment().apply { }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().applicationContext as App).appComponent.accountComponent().create()
            .inject(this)
        binding = FragmentSelectInterestsBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        adapter = CategoryInterestAdapter(list, toggleListener)
        binding.categoryGridView.setGrid(requireContext(), 2)
        binding.categoryGridView.adapter = adapter
        binding.saveSelectionButton.setOnClickListener { navigateNext() }
        binding.categoryGridView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (categoriesOver || list.isEmpty()) return

                val lastPostPosition =
                    (binding.categoryGridView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (list.size == lastPostPosition + 1 && !presenter.isCategoriesLoading) {
                    presenter.fetch(offset = list.size)
                }
            }
        })
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

    override fun onFetched(offset: Int, response: GetCategoriesResponse?) {
        response?.featured?.let {
            if (it.size > categoryDynamicPageSize) {
                categoryDynamicPageSize = it.size
            }
            if (offset == 0)
                list.clear()
            list.addAll(it)
            adapter.notifyDataSetChanged()
            categoriesOver = it.size < categoryDynamicPageSize
        }
    }

    override fun onToggled() {

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyView()
    }
}
