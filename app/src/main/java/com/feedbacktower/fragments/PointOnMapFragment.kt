package com.feedbacktower.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.databinding.FragmentPointOnMapBinding
import com.feedbacktower.network.manager.LocationManager
import com.feedbacktower.util.LocationUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.anko.toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.Location
import com.feedbacktower.util.PermissionUtils
import com.feedbacktower.util.zoomToLocation


class PointOnMapFragment : Fragment(), OnMapReadyCallback {
    private val TAG = "PointOnMap"
    private var markedLocation: LatLng? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var googleMap: GoogleMap? = null
    private val args: PointOnMapFragmentArgs by navArgs()
    private var MAX_GET_LOCATION_RETRY = 2

    override fun onResume() {
        super.onResume()
        if (!PermissionUtils.locationPermissionsGranted(requireActivity())) {
            PermissionUtils.requestLocationPermission(requireActivity())
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPointOnMapBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentPointOnMapBinding) {
        binding.onContinue = View.OnClickListener { saveLocation(binding) }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(gmap: GoogleMap) {
        googleMap = gmap
        googleMap?.setOnCameraIdleListener {
            markedLocation = googleMap?.cameraPosition?.target ?: return@setOnCameraIdleListener
        }
        enableMyLocation()
        val oldLocation = AppPrefs.getInstance(requireContext()).user?.business?.location
        if (oldLocation?.latitude != null && oldLocation.longitude != null) {
            googleMap?.zoomToLocation(LatLng(oldLocation.latitude!!, oldLocation.longitude!!), 20f)
            return
        }

        if (LocationUtils.getInstance().isLocationEnabled(requireContext())) {
            getLastLocation()
        } else {
            showLocationSettingsDialog()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!PermissionUtils.locationPermissionsGranted(requireActivity())) {
            PermissionUtils.requestLocationPermission(requireActivity())
            return
        }

        googleMap?.isMyLocationEnabled = true
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        Log.d(TAG, "Fetching location...")
        fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
            if (location == null) {
                if (MAX_GET_LOCATION_RETRY > 0) {
                    Handler().postDelayed({
                        getLastLocation()
                        MAX_GET_LOCATION_RETRY--
                    }, 1000)
                } else {
                    requireContext().toast("Could not detect location")
                }
                return@addOnSuccessListener
            }
            val currLocation = LatLng(location.latitude, location.longitude)
            LocationUtils.getInstance().lastKnownLocation = currLocation
            googleMap?.zoomToLocation(currLocation, 12f)
        }
    }

    private fun showLocationSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable location")
            .setMessage("Location must be enabled to get your current location")
            .setPositiveButton("OPEN SETTINGS") { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1099)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun saveLocation(binding: FragmentPointOnMapBinding) {
        if (markedLocation == null) {
            requireContext().toast("Mark your business location by moving map")
            return
        }
        markedLocation?.let {
            binding.isLoading = true
            LocationManager.getInstance()
                .saveBusinessLocation(it) { _, error ->
                    binding.isLoading = false
                    if (error != null) {
                        requireContext().toast(error.message ?: getString(R.string.default_err_message))
                        return@saveBusinessLocation
                    }
                    Log.d(TAG, "BusinessLocBefore: ${AppPrefs.getInstance(requireContext()).user?.business?.location}")
                    AppPrefs.getInstance(requireContext())
                        .apply {
                            user = user?.apply {
                                business?.apply {
                                    location = Location(listOf(it.latitude, it.longitude), "permanent")
                                }
                            }
                        }
                    Log.d(TAG, "BusinessLocLater: ${AppPrefs.getInstance(requireContext()).user?.business?.location}")
                    navigateNext()
                }
        }
    }

    private fun navigateNext() {
        if (!args.edit) {
            PointOnMapFragmentDirections.actionPointOnMapFragmentToBusinessSetup2Fragment().let { dir ->
                findNavController().navigate(dir)
            }
            return
        }
        findNavController().navigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
