package com.feedbacktower.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.feedbacktower.adapters.CityListAdapter
import com.feedbacktower.data.models.City
import com.feedbacktower.databinding.FragmentSelectCityBinding
import com.feedbacktower.util.setVertical

class SelectCityFragment : Fragment() {

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
            SelectCityFragmentDirections.actionSelectCityFragmentToSelectInterestsFragment().let {
                findNavController().navigate(it)
            }
        }
        return binding.root
    }

}
