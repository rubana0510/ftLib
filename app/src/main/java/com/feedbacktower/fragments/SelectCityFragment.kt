package com.feedbacktower.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.adapters.CityListAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.City
import com.feedbacktower.databinding.FragmentSelectCityBinding
import com.feedbacktower.network.manager.LocationManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast

class SelectCityFragment : Fragment() {
    private lateinit var adapter: CityListAdapter
    private val args: SelectCityFragmentArgs by navArgs()
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

        adapter = CityListAdapter(object : CityListAdapter.Listener {
            override fun onCityClick(city: City) {
                ProfileManager.getInstance()
                    .updateCity(city.id.toString()) { response, error ->
                        if (error == null) {

                            AppPrefs.getInstance(requireContext()).setValue("USER_CITY", city.id.toString())
                            AppPrefs.getInstance(requireContext()).setValue("CITY", city.name)
                            if(args.onboarding){
                                SelectCityFragmentDirections.actionSelectCityFragmentToSelectInterestsFragment().let {
                                    findNavController().navigate(it)
                                }
                            }else{
                                findNavController().navigateUp()
                            }
                        }else{
                            requireContext().toast("Error saving City")
                        }
                    }

            }

        })
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
