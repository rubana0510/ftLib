package com.feedbacktower.ui.profile.setup2


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.databinding.FragmentBusinessSetup2Binding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.ui.plans.SubscriptionPlansScreen
import com.feedbacktower.util.*
import org.jetbrains.anko.toast
import javax.inject.Inject


class BusinessSetup2Fragment : BaseViewFragmentImpl(), BusinessSetup2Contract.View {
    @Inject
    lateinit var presenter: BusinessSetup2Presenter
    @Inject
    lateinit var appPrefs: ApplicationPreferences

    private lateinit var binding: FragmentBusinessSetup2Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBusinessSetup2Binding.inflate(inflater, container, false)
        presenter.attachView(this)
        initUi()
        return binding.root
    }

    private fun initUi() {
        binding.business = appPrefs.user?.business

        binding.onContinueClick = View.OnClickListener {
            if (valid(
                    binding.officeAddressInput.text.toString().trim(),
                    binding.officeContactInput.text.toString().trim(),
                    binding.officeWebsiteInput.text.toString().trim()
                )
            ) {
                presenter.updateDetails(
                    binding.officeAddressInput.text.toString().trim(),
                    binding.officeContactInput.text.toString().trim(),
                    binding.officeWebsiteInput.text.toString().trim()
                )
            }
        }
    }

    private fun valid(address: String, contact: String, website: String): Boolean {
        binding.officeAddressLayout.error = null
        binding.officeWebsiteLayout.error = null
        binding.officeContactLayout.error = null

        return when {
            address.isEmpty() -> {
                binding.officeAddressLayout.error = "Enter valid business address"
                false
            }
            contact.isEmpty() || contact.length != 10 -> {
                binding.officeContactLayout.error = "Enter valid contact number"
                false
            }
            website.isNotEmpty() -> {
                if (!website.validWebsite()) {
                    binding.officeWebsiteLayout.error = "Enter valid website"
                    false
                } else {
                    true
                }
            }
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


    override fun showProgress() {
        super.showProgress()
        binding.continueButton.disable()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        binding.continueButton.enable()
    }

    override fun onDetailsUpdated(address: String, contact: String, website: String) {
        navigateNext()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireActivity().toast(error.message)
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}


