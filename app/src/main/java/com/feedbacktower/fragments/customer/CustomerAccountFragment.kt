package com.feedbacktower.fragments.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.R
import com.feedbacktower.adapters.AccountOptionsAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.local.models.AccountOption
import com.feedbacktower.databinding.FragmentCustomerAccountBinding
import com.feedbacktower.ui.ProfileSetupScreen
import com.feedbacktower.ui.SplashScreen
import com.feedbacktower.util.launchActivity


class CustomerAccountFragment : Fragment() {

    private val TAG = "CustomerAccountFrag"
    private lateinit var accountOptionsView: RecyclerView

    private lateinit var accountOptionsAdapter: AccountOptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentCustomerAccountBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentCustomerAccountBinding) {

        accountOptionsView = binding.accountOptionsView

        //setup options list
        accountOptionsView.setHasFixedSize(true)
        accountOptionsAdapter = AccountOptionsAdapter(onItemSelected)
        accountOptionsView.adapter = accountOptionsAdapter
        submitOptions()

        binding.userFullName = AppPrefs.getInstance(requireContext()).user?.firstName +" " +AppPrefs.getInstance(requireContext()).user?.lastName
        binding.editProfileButtonClicked = View.OnClickListener {
            requireActivity().launchActivity<ProfileSetupScreen>()
        }
    }

    private fun submitOptions() {
        val options = listOf(
            AccountOption(2, "My Reviews", "Reviews given by you", R.drawable.ic_post_like_filled),
            AccountOption(3, "My Suggestions", "Suggestions given by you", R.drawable.ic_post_like_filled),
            AccountOption(4, "Help", "Help and FAQs", R.drawable.ic_post_like_filled),
            AccountOption(5, "Logout", "Logout from ${getString(R.string.app_name)}", R.drawable.ic_post_like_filled)
        )
        accountOptionsAdapter.submitList(options)
    }

    private val onItemSelected = { option: AccountOption ->
        Log.d(TAG, "${option.title} selected")
        when (option.id) {
            1 -> {

            }
            2 -> {
                val d = CustomerAccountFragmentDirections.actionNavigationCustomerAccountToReviewsFragment(null)
                findNavController().navigate(d)
            }
            3 -> {
                val d = CustomerAccountFragmentDirections.actionNavigationCustomerAccountToSuggestionsFragment()
                d.mySuggestions = true
                findNavController().navigate(d)
            }
            4 -> {

            }
            5 -> {
                AppPrefs.getInstance(requireContext()).authToken = null
                AppPrefs.getInstance(requireContext()).user = null
                requireActivity().launchActivity<SplashScreen> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
        }
    }
}
