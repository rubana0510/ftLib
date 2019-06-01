package com.feedbacktower.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import kotlinx.android.synthetic.main.fragment_select_city.*
import org.jetbrains.anko.toast

class SelectCityFragment : Fragment() {
    private lateinit var adapter: CityListAdapter
    private lateinit var queryInput: EditText
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
                            AppPrefs.getInstance(requireContext()).apply {
                                user = user?.apply {
                                    this.city = city
                                }
                            }
                            navigateNext()
                        } else {
                            requireContext().toast("Error saving City")
                        }
                    }

            }

        })
        binding.queryInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrEmpty()) return

                val input = text.toString().trim()
                LocationManager.getInstance()
                    .autocomplete(input) { response, throwable ->
                        Log.d("Search city", response.toString())
                        val list = ArrayList<City>()
                        response?.predictions?.forEach {
                            list.add(City(0, it.reference, 0, it.description))
                        }
                        adapter.submitList(list)
                    }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        binding.cityList.setVertical(requireContext())
        binding.cityList.adapter = adapter

        fetchCities(binding)
        return binding.root
    }

    private fun navigateNext() {
        if (!args.edit) {
            SelectCityFragmentDirections.actionSelectCityFragmentToSelectInterestFragment().let {
                findNavController().navigate(it)
            }
            return
        }
        findNavController().navigateUp()
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
