package com.feedbacktower.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentAccountSelectionTypeBinding
import com.feedbacktower.network.manager.AuthManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.service.ApiServiceDescriptor
import com.feedbacktower.network.utils.makeRequest
import com.feedbacktower.ui.BusinessProfileSetupScreen
import com.feedbacktower.ui.CustomerMainActivity
import com.feedbacktower.util.launchActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import java.lang.IllegalArgumentException

class AccountTypeSelectionFragment : Fragment() {
    private var onboarding = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAccountSelectionTypeBinding.inflate(inflater, container, false)
        onboarding = arguments?.getBoolean(BusinessProfileSetupScreen.ONBOARDING_KEY, true)
            ?: throw  IllegalArgumentException("Invalid args")
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentAccountSelectionTypeBinding) {
        binding.onCutomerContinue = View.OnClickListener {
            if (!onboarding)
                requireActivity().finish()
            else
                continueAsCustomer()
        }
        binding.onBusinessContinue = View.OnClickListener { registerAsBusiness() }
        binding.user = AppPrefs.getInstance(requireContext()).user
    }

    private fun registerAsBusiness() {
        AuthManager.getInstance()
            .registerAsBusiness { _, error ->
                if (error != null) {
                    requireActivity().toast(error.message ?: getString(R.string.default_err_message))
                } else {
                    AccountTypeSelectionFragmentDirections.actionAccountTypeSelectionFragment2ToBusinessSetup1Fragment()
                        .let {
                            findNavController().navigate(it)
                        }
                }
            }
    }

    private fun continueAsCustomer() {
        ProfileManager.getInstance()
            .continueAsCustomer { _, error ->
                if (error != null) {
                    requireActivity().toast(error.message ?: getString(R.string.default_err_message))
                } else {
                    AppPrefs.getInstance(requireContext()).apply {
                        user = user.apply {
                            this!!.profileSetup = true
                        }
                    }
                    requireActivity().launchActivity<CustomerMainActivity> {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                }
            }
    }

}
