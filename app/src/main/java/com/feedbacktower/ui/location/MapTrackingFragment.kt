package com.feedbacktower.ui.location

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentMapTrackingBinding
import com.feedbacktower.util.*
import com.feedbacktower.util.toRelativeTime
import com.feedbacktower.utilities.tracker.TrackerService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.toast


class MapTrackingFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val TRACKING_ON_KEY = "tracking_on"
    }

    private val TAG = "MapTracking"
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var googleMap: GoogleMap? = null
    private var MAX_GET_LOCATION_RETRY = 2
    private var trackingService: TrackerService? = null
    private lateinit var binding: FragmentMapTrackingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMapTrackingBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    private fun initUi() {
        val intent = Intent(requireActivity().application, TrackerService::class.java)
        requireActivity().application.startService(intent)
        requireActivity().application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        if (AppPrefs.getInstance(requireContext()).getValue(TRACKING_ON_KEY, false)) {
            showTrackingOn()
            binding.trackingSwitch.isChecked = true
        } else {
            showTrackingOff()
            binding.trackingSwitch.isChecked = false
        }
        binding.trackingSwitch.setOnClickListener {
            if (binding.trackingSwitch.isChecked) {
                AppPrefs.getInstance(requireContext()).setValue(TRACKING_ON_KEY, true)
                showTrackingOn()
                trackingService?.startTracking()
            } else {
                AppPrefs.getInstance(requireContext()).setValue(TRACKING_ON_KEY, false)
                showTrackingOff()
                trackingService?.stopTracking()
            }
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(gmap: GoogleMap) {

        googleMap = gmap
        googleMap?.setOnCameraIdleListener {

        }
        if (!PermissionUtils.locationPermissionsGranted(requireActivity())) {
            PermissionUtils.requestLocationPermission(requireActivity())
            return
        }
        enableMyLocation()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(LocationBroadcastReceiver(), IntentFilter(TrackerService.INTENT_FILTER))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext())
            .unregisterReceiver(LocationBroadcastReceiver())
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        googleMap?.isMyLocationEnabled = true
        if (LocationUtils.getInstance().isLocationEnabled(requireContext())) {
            getLastLocation()
        } else {
            showLocationSettingsDialog()
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!PermissionUtils.locationPermissionsGranted(requireActivity())) {
            return
        }
        enableMyLocation()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            Log.d(TAG, "serviceConnection: onServiceConnected: $name")
            if (name.endsWith("TrackerService")) {
                trackingService = (service as TrackerService.LocationServiceBinder).service
                Log.d(TAG, "serviceConnection: Initialized")
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.d(TAG, "serviceConnection: onServiceDisconnected: $className")
            if (className.className == "TrackerService") {
                trackingService = null
                Log.d(TAG, "serviceConnection: Disconnected")
            }
        }
    }

    private fun showTrackingOn() {
        binding.title.text = "Location tracking is ON"
        binding.message.text = getString(R.string.location_tracking_hint)
        binding.box.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorActivated))
    }

    private fun showTrackingOff() {
        binding.title.text = "Location tracking is OFF"
        binding.message.text = getString(R.string.location_tracking_hint)
        binding.box.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorDeactivated))
    }

    inner class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            Log.d(TAG, "LocationBroadcastReceiver:  onReceive: ")
            intent?.let {
                val latitude = intent.getDoubleExtra("LAT", 0.0)
                val longitude = intent.getDoubleExtra("LONG", 0.0)
                Log.d(TAG, "LocationBroadcastReceiver:  onReceive: Location: $latitude, $longitude")
                if (latitude != 0.0 && longitude != 0.0) {
                    googleMap?.clear()
                    val loc = LatLng(latitude, longitude)
                    googleMap?.addMarker(MarkerOptions().position(loc).title("Location Saved at: ${System.currentTimeMillis().toRelativeTime()}"))
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLng(loc))
                    googleMap?.zoomToLocation(loc, 20f)
                }
            }
        }
    }
}
