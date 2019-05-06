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
import com.feedbacktower.network.manager.LocationManager
import com.feedbacktower.util.setVertical

class SelectCityFragment : Fragment() {
    private lateinit var adapter: CityListAdapter

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

        adapter = CityListAdapter()
        binding.cityList.setVertical(requireContext())
        binding.cityList.adapter = adapter

        fetchCities(binding)
        return binding.root
    }

    private fun fetchCities(binding: FragmentSelectCityBinding) {
        LocationManager.getInstance()
            .getCities { response, error ->
                if (error == null) {
                    adapter.submitList(response?.cities)
                }
            }
    }

}
