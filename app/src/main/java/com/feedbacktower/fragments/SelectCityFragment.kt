package com.feedbacktower.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.feedbacktower.adapters.CategoryListAdapter
import com.feedbacktower.adapters.CityListAdapter
import com.feedbacktower.data.models.City
import com.feedbacktower.databinding.FragmentSelectCityBinding
import com.feedbacktower.util.InjectorUtils
import com.feedbacktower.util.setVertical
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
        val adapter = CityListAdapter()
        binding.cityList.setVertical(requireContext())
        binding.cityList.adapter = adapter
        adapter.submitList(
            listOf(
                City("1", "Panjim", "1", "Goa"),
                City("2", "Mapusa", "1", "Goa"),
                City("3", "Margao", "1", "Goa"),
                City("4", "Vasco", "1", "Goa"),
                City("5", "Porvorim", "1", "Goa")
            )
        )
        binding.continueButton.setOnClickListener {
            SelectCityFragmentDirections.actionSelectCityFragmentToAccountTypeSelectionFragment().let {
                findNavController().navigate(it)
            }
        }
        return binding.root
    }

    private fun subscribeUi(adapter: CategoryListAdapter) {
        viewModel.fetchBusinessCategories()
        viewModel.categoriesLiveData.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}
