package com.feedbacktower.ui.city

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.App
import com.feedbacktower.adapters.AutoCompleteAdapter
import com.feedbacktower.adapters.CityListAdapter
import com.feedbacktower.data.models.City
import com.feedbacktower.data.models.Place
import com.feedbacktower.databinding.FragmentSelectCityBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.GetCitiesResponse
import com.feedbacktower.network.models.PlaceDetailsResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast
import javax.inject.Inject

class SelectCityFragment : BaseViewFragmentImpl(), SelectCityContract.View {
    @Inject
    lateinit var presenter: SelectCityPresenter
    private lateinit var binding: FragmentSelectCityBinding
    private lateinit var adapter: CityListAdapter
    private lateinit var autoCompleteAdapter: AutoCompleteAdapter
    private lateinit var queryInput: EditText
    private lateinit var listView: RecyclerView
    private val cityList: ArrayList<City> = ArrayList()
    private val placeList: ArrayList<Place> = ArrayList()
    private val args: SelectCityFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().applicationContext as App).appComponent.accountComponent().create().inject(this)
        binding = FragmentSelectCityBinding.inflate(inflater, container, false)
        adapter = CityListAdapter(cityList, object : CityListAdapter.Listener {
            override fun onCityClick(city: City) {
                saveCityToProfile(city)
            }
        })

        autoCompleteAdapter = AutoCompleteAdapter(placeList, object : AutoCompleteAdapter.Listener {
            override fun onAutoCompleteClick(place: Place) {
                presenter.getPlaceDetails(place.placeId)
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
        presenter.attachView(this)
        fetchCities()
        return binding.root
    }


    private fun saveCityToProfile(city: City) {
        if (!args.business) {
            presenter.saveUserCity(city)
        } else {
            presenter.saveBusinessCity(city)
        }
    }


    private fun fetchCities(keyword: String = "") {
        presenter.fetchCities(keyword)
    }

    override fun onPlaceDetailsFetched(placeDetails: PlaceDetailsResponse.Result) {
        parseCityState(placeDetails)
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
                    presenter.createNewCityState(city!!, state!!)
                }
                .setNegativeButton("CANCEL") { _, _ ->
                    fetchCities()
                }
                .show()
        }
    }

    override fun onCityStateCreated(city: City) {
        saveCityToProfile(city)
    }

    override fun onCitiesFetched(response: GetCitiesResponse?) {
        response?.cities?.let {
            cityList.clear()
            cityList.addAll(it)
            listView.adapter = adapter
        }
    }

    override fun onPlacesFetched(places: List<Place>?) {
        places?.let {
            placeList.clear()
            placeList.addAll(it)
            listView.adapter = autoCompleteAdapter
        }
    }

    override fun onBusinessCitySaved(city: City) {
        navigateNext()
    }

    override fun onUserCitySaved(city: City) {
        navigateNext()
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
        requireActivity().toast(error.message)
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

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
