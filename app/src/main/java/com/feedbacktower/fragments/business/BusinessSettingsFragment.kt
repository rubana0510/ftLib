package com.feedbacktower.fragments.business


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import androidx.preference.*
import com.feedbacktower.R
import com.feedbacktower.utilities.tracker.TrackerService
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
