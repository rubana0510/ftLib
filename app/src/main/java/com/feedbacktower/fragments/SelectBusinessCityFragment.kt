package com.feedbacktower.fragments

import android.app.AlertDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.adapters.AutoCompleteAdapter
import com.feedbacktower.adapters.CityListAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.City
import com.feedbacktower.data.models.Place
import com.feedbacktower.network.models.PlaceDetailsResponse
import com.feedbacktower.databinding.FragmentSelectCityBinding
import com.feedbacktower.network.manager.LocationManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.models.AutoCompleteResponse
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast

class SelectBusinessCityFragment : Fragment() {
    private lateinit var adapter: CityListAdapter
    private lateinit var autoCompleteAdapter: AutoCompleteAdapter
    private lateinit var queryInput: EditText
    private lateinit var listView: RecyclerView
    private lateinit var binding: FragmentSelectCityBinding
    private val args: SelectCityFragmentArgs by navArgs()

    companion object {
        fun getInstance(): SelectBusinessCityFragment {
            val fragment = SelectBusinessCityFragment()
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectCityBinding.inflate(inflater, container, false)
        val context = context ?: return binding.root

        adapter = CityListAdapter(object : CityListAdapter.Listener {
            override fun onCityClick(city: City) {
                saveCityToProfile(city)
            }

        })

        autoCompleteAdapter = AutoCompleteAdapter(object : AutoCompleteAdapter.Listener {
            override fun onAutoCompleteClick(place: Place) {
                getPlaceDetails(place.placeId)
                //requireContext().toast("Selected ${place.description}")
            }
        })

        binding.queryInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(text: Editable?) {
                if (text.isNullOrEmpty()) {
                    fetchCities()
                    return
                }

                val keyword = text.toString().trim()

                //search in ft db
                fetchCities(keyword)

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
        listView = binding.cityList
        listView.setVertical(requireContext())
        listView.adapter = adapter

        fetchCities()
        return binding.root
    }


    private fun saveCityToProfile(city: City) {
        requireContext().toast("Please wait, saving city")
        binding.isLoading = true
        ProfileManager.getInstance()
            .updateBusinessCity(city.id.toString()) { response, error ->
                binding.isLoading = false
                if (error == null) {
                    AppPrefs.getInstance(requireContext()).apply {
                        user = user?.apply {
                            this.business = business?.apply {
                                this.city = city
                            }
                        }
                    }
                    navigateNext()
                } else {
                    requireContext().toast("Error saving City")
                }
            }
    }

    private fun fetchCities(keyword: String = "") {
        binding.noResults = false
        binding.isLoading = true
        LocationManager.getInstance()
            .getCities(keyword) { response, error ->
                binding.isLoading = false
                if (error == null && response != null) {
                    if (response.cities.isEmpty()) {
                        performNewCitiesSearch(keyword)
                        return@getCities
                    }
                    submitToCityAdapter(response.cities)
                }
            }
    }

    private fun performNewCitiesSearch(input: String) {
        binding.noResults = false
        submitToCityAdapter(null)
        binding.isLoading = true
        LocationManager.getInstance()
            .autocomplete(input) { response, error ->
                binding.isLoading = false
                if (error != null) {
                    binding.noResults = true
                } else {
                    val list = getPlaces(response?.predictions)
                    submitToAutoCompleteAdapter(list)
                }
            }
    }

    private fun getPlaceDetails(placeId: String) {
        binding.noResults = false
        binding.isLoading = true
        LocationManager.getInstance()
            .placeDetails(placeId) { response, error ->
                if (error != null || response == null) {
                    binding.isLoading = false
                    fetchCities()
                } else {
                    parseCityState(response.result)
                }
            }
    }

    private fun parseCityState(result: PlaceDetailsResponse.Result) {
        var city: String? = null
        var state: String? = null
        result.addressComponents.forEach {
            if (it.types.isNotEmpty() && it.types[0] == "country") {
                if (it.longName != "India") {
                    AlertDialog.Builder(requireContext())
                        .setTitle("We are unavailable in ${it.longName}")
                        .setMessage("Please choose some other city")
                        .setPositiveButton("OKAY", null)
                        .show()
                    return
                }
            }
            if (it.types.isNotEmpty() && it.types[0] == "administrative_area_level_1") {
                state = it.longName
            } else if (it.types.isNotEmpty() && it.types[0] == "locality") {
                city = it.longName
            }
        }

        if (city == null) {
            result.addressComponents.forEach {
                if (it.types.isNotEmpty() && it.types[0] == "sublocality_level_1") {
                    city = it.longName
                }
            }
        }

        if (city == null) {
            requireContext().toast("City not found for this place")
            fetchCities()
        } else if (state == null) {
            requireContext().toast("State not found for this place")
            fetchCities()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Save city as..")
                .setMessage("$city, $state")
                .setPositiveButton("SAVE") { _, _ ->
                    createNewCityState(city!!, state!!)
                }
                .setNegativeButton("CANCEL") { _, _ ->
                    fetchCities()
                }
                .show()
        }
    }

    private fun createNewCityState(city: String, state: String) {
        binding.isLoading = true
        requireContext().toast("Adding city please wait")
        LocationManager.getInstance()
            .addNewCity(city, state) { response, error ->
                binding.isLoading = false
                if (error != null || response == null) {
                    requireContext().toast("Error saving city")
                    fetchCities()
                } else {
                    saveCityToProfile(response.city)
                }
            }
    }

    private fun getPlaces(predictions: List<AutoCompleteResponse.Prediction>?): List<Place> {
        val list = ArrayList<Place>()
        predictions?.forEach {
            list.add(Place(it.placeId, it.description))
            Log.d("SelectCityFrag", "Adding: ${it.description}")
        }
        return list.toMutableList()
    }

    private fun submitToCityAdapter(list: List<City>?) {
        adapter.submitList(list)
        listView.adapter = adapter
    }

    private fun submitToAutoCompleteAdapter(list: List<Place>?) {
        autoCompleteAdapter.submitList(list)
        listView.adapter = autoCompleteAdapter
    }


    private fun navigateNext() {
        /*if (!args.edit) {
            SelectCityFragmentDirections.actionSelectCityFragmentToSelectInterestFragment().let {
                findNavController().navigate(it)
            }
            return
        }*/
        findNavController().navigateUp()
    }


}
