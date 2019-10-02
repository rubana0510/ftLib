package com.feedbacktower.fragments.business


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentBusinessSetup2Binding
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.plans.SubscriptionPlansScreen
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.noValidWebsite
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.toast


class BusinessSetup2Fragment : Fragment() {
    private lateinit var contactLayout: TextInputLayout
    private lateinit var websiteLayout: TextInputLayout
    private lateinit var addressLayout: TextInputLayout
    private lateinit var continueButton: Button
    private lateinit var contactInput: TextInputEditText
    private lateinit var websiteInput: TextInputEditText
    private lateinit var addressInput: TextInputEditText

    private val args: BusinessSetup2FragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentBusinessSetup2Binding.inflate(inflater, container, false)

        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentBusinessSetup2Binding) {

        binding.business = AppPrefs.getInstance(requireContext()).user?.business

        addressLayout = binding.officeAddressLayout
        contactLayout = binding.officeContactLayout
        websiteLayout = binding.officeWebsiteLayout

        addressInput = binding.officeAddressInput
        contactInput = binding.officeContactInput
        websiteInput = binding.officeWebsiteInput

        continueButton = binding.continueButton

        binding.onContinueClick = View.OnClickListener {
            if (valid(
                    addressInput.text.toString().trim(),
                    contactInput.text.toString().trim(),
                    websiteInput.text.toString().trim()
                )
            ) {
                updateDetails(
                    addressInput.text.toString().trim(),
                    contactInput.text.toString().trim(),
                    websiteInput.text.toString().trim()
                )
            }
        }
    }

    private fun hideLoading() {
        continueButton.enable()
    }

    private fun showLoading() {
        continueButton.disable()
    }

    private fun valid(address: String, contact: String, website: String): Boolean {
        addressLayout.error = null
        websiteLayout.error = null
        contactLayout.error = null

        return when {
            address.isEmpty() -> {
                addressLayout.error = "Enter valid business address"
                false
            }
            contact.isEmpty() || contact.length != 10 -> {
                contactLayout.error = "Enter valid contact number"
                false
            }
           /* website.isEmpty() || website.noValidWebsite() -> {
                websiteLayout.error = "Enter valid website"
                false
            }*/
            else -> true
        }
    }


    private fun navigateNext() {
        val args: BusinessSetup2FragmentArgs by navArgs()
        if (!args.edit) {
            requireActivity().launchActivity<SubscriptionPlansScreen>()
            return
        }
        findNavController().navigateUp()
    }

    private fun updateDetails(
        address: String,
        contact: String,
        website: String
    ) {
        showLoading()
        ProfileManager.getInstance()
            .updateBusinessAddressDetails(
                address,
                contact,
                website,
                null, null
            )
            { response, error ->
                hideLoading()
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@updateBusinessAddressDetails
                }

                navigateNext()

            }
    }
}


