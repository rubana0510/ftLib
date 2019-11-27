package com.feedbacktower.ui.location.update

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.FragmentPointOnMapBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.LocationUtils
import com.feedbacktower.util.permissions.PermissionUtils
import com.feedbacktower.util.toLocation
import com.feedbacktower.util.zoomToLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import org.jetbrains.anko.toast
import javax.inject.Inject


class PointOnMapFragment : BaseViewFragmentImpl(), BusinessLocationContract.View, OnMapReadyCallback {
    private val TAG = "PointOnMap"
    @Inject
    lateinit var presenter: BusinessLocationPresenter
    @Inject
    lateinit var user: User
    private lateinit var binding: FragmentPointOnMapBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.accountComponent().create().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPointOnMapBinding.inflate(inflater, container, false)

        initUi()
        presenter.attachView(this)
        return binding.root
    }

    private fun initUi() {
        binding.onContinue = View.OnClickListener { saveLocation() }
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
        val oldLocation = user.business?.location
        if (oldLocation?.latitude != null && oldLocation.longitude != null) {
            googleMap?.zoomToLocation(LatLng(oldLocation.latitude!!, oldLocation.longitude!!), 15f)
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
            val oldLocation = user.business?.location
            if (oldLocation?.latitude == null || oldLocation.longitude == null) {
                googleMap?.zoomToLocation(currLocation, 12f)
            }
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

    override fun showProgress() {
        super.showProgress()
        binding.isLoading = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        binding.isLoading = false
    }

    override fun onSaved(savedLocation: LatLng) {
        navigateNext()
    }

    private fun saveLocation() {
        if (markedLocation == null) {
            requireContext().toast("Mark your business location by moving map")
            return
        }
        markedLocation?.let {
            presenter.saveLocation(it)
        }
    }

    private fun navigateNext() {
        if (!args.edit) {
            PointOnMapFragmentDirections.actionPointOnMapFragmentToBusinessSetup2Fragment()
                .let { dir ->
                    findNavController().navigate(dir)
                }
            return
        }
        findNavController().navigateUp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.locationPermissionsGranted(requireActivity())) {
            enableMyLocation()
        }
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireActivity().toast(error.message)
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1099) {
            getLastLocation()
        }
    }
}
