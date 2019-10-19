package com.feedbacktower.ui.account.type_selection

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentAccountSelectionTypeBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.main.CustomerMainActivity
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import com.feedbacktower.util.launchActivity
import org.jetbrains.anko.toast

class AccountTypeSelectionFragment : BaseViewFragmentImpl(), AccountTypeSelectionContract.View {
    private lateinit var binding: FragmentAccountSelectionTypeBinding
    private lateinit var presenter: AccountTypeSelectionPresenter
    private val args: AccountTypeSelectionFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAccountSelectionTypeBinding.inflate(inflater, container, false)
        initUi()
        presenter = AccountTypeSelectionPresenter()
        presenter.attachView(this)
        return binding.root
    }

    private fun initUi() {
        binding.onCutomerContinue = View.OnClickListener {
            if (args.finishActivity)
                requireActivity().finish()
            else
                presenter.continueAsCustomer()
        }
        binding.onBusinessContinue = View.OnClickListener { presenter.registerAsBusiness() }
        binding.user = AppPrefs.getInstance(requireContext()).user
    }

    override fun showProgress() {
        super.showProgress()
        binding.businessButton.disable()
        binding.customerButton.disable()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        binding.businessButton.enable()
        binding.customerButton.enable()
    }

    override fun onBusinessRegistered() {
        AccountTypeSelectionFragmentDirections.actionAccountTypeSelectionFragmentToBusinessSetup1Fragment()
            .let {
                findNavController().navigate(it)
            }
    }

    override fun onContinueCustomerResponse() {
        AppPrefs.getInstance(requireContext()).apply {
            user = user?.apply {
                profileSetup = true
            }
        }
        requireActivity().launchActivity<CustomerMainActivity> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

}
