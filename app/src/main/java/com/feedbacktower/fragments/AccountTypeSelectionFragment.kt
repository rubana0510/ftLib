package com.feedbacktower.fragments
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
import com.feedbacktower.ui.BusinessProfileSetupScreen
import com.feedbacktower.ui.CustomerMainActivity
import com.feedbacktower.util.launchActivity
import org.jetbrains.anko.toast

class AccountTypeSelectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAccountSelectionTypeBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentAccountSelectionTypeBinding) {
        binding.onCutomerContinue = View.OnClickListener {  requireActivity().launchActivity<CustomerMainActivity>()}
        binding.onBusinessContinue = View.OnClickListener {  registerAsBusiness()}
        binding.user = AppPrefs.getInstance(requireContext()).user
    }

    private fun registerAsBusiness() {
        AuthManager.getInstance()
            .registerAsBusiness{_,error->
                if(error != null){
                    requireActivity().toast(error?.message?:getString(R.string.default_err_message))
                }else{
                    requireActivity().launchActivity<BusinessProfileSetupScreen>()
                }
            }
    }


}
