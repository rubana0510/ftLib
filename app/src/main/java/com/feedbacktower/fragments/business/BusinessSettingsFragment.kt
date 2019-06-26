package com.feedbacktower.fragments.business


import android.content.*
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import androidx.preference.*
import com.feedbacktower.R
import com.feedbacktower.utilities.tracker.TrackerService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import org.jetbrains.anko.toast

/**
 * A simple [Fragment] subclass.
 *
 */
class BusinessSettingsFragment : PreferenceFragmentCompat() {
    private var trackinService: TrackerService? = null
    private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
        val stringValue = value.toString()

        if (preference is ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            val listPreference = preference
            val index = listPreference.findIndexOfValue(stringValue)

            // Set the summary to reflect the new value.
            preference.setSummary(
                if (index >= 0)
                    listPreference.entries[index]
                else
                    null
            )
            if (preference.key == "interval_list") {


            } else if (preference.key == "app_theme") {
                //activity?.recreate()
            }
        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.summary = stringValue
        }
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(requireActivity().getApplication(), TrackerService::class.java)
        addPreferencesFromResource(R.xml.business_settings)
        val trackMePref: SwitchPreference = findPreference("enable_trackme") as SwitchPreference
        //checkGps()

        trackMePref.setOnPreferenceChangeListener { preference, newValue ->
            if (!trackMePref.isChecked){
                val innntent = Intent(requireActivity().getApplication(), TrackerService::class.java)
                requireActivity().getApplication().startService(innntent)
                requireActivity().getApplication().bindService(innntent, serviceConnection, Context.BIND_AUTO_CREATE)
                trackMePref.isChecked = true
                trackinService?.startTracking()
                requireContext().toast("Tracking is on")
            }else{
                trackMePref.isChecked = false
                trackinService?.stopTracking()
                requireContext().toast("Tracking is off")
            }
            return@setOnPreferenceChangeListener false
        }
    }
    private fun checkGps() {
        val googleApiClient = GoogleApiClient.Builder(requireContext())
            .addApi(LocationServices.API)
            .build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(50000)
        locationRequest.setFastestInterval(10000 / 2)
        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        settingsBuilder.setAlwaysShow(true)
        val pendingResult =
            LocationServices.getSettingsClient(requireContext()).checkLocationSettings(settingsBuilder.build())
        pendingResult.addOnCompleteListener { task ->
            try{
                val response = task.getResult(ApiException::class.java)
            }catch (e: ApiException){
                when(e.statusCode){
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try{
                            (e as ResolvableApiException).startResolutionForResult(requireActivity(), 2999)
                        }catch (e2: IntentSender.SendIntentException){

                        }
                    }
                }
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    }

    private fun bindPreferenceSummaryToValue(preference: Preference) {
        // Set the listener to watch for value changes.
        preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(
            preference,
            PreferenceManager
                .getDefaultSharedPreferences(preference.context)
                .getString(preference.key, "")
        )
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name.endsWith("TrackerService")) {
                trackinService = (service as TrackerService.LocationServiceBinder).getService()
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == "TrackerService") {
                trackinService = null
            }
        }
    }
}
