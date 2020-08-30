package com.feedbacktower.ui.account.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.adapters.AccountOptionsAdapter
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.local.models.AccountOption
import com.feedbacktower.databinding.FragmentCustomerAccountBinding
import com.feedbacktower.ui.profile.BusinessProfileSetupScreen
import com.feedbacktower.ui.splash.SplashScreen
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.showAppInStore
import javax.inject.Inject


class CustomerAccountFragment : Fragment() {

    private val TAG = "CustomerAccountFrag"

    @Inject
    lateinit var appPrefs: ApplicationPreferences

    private lateinit var accountOptionsView: RecyclerView
    private lateinit var accountOptionsAdapter: AccountOptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().applicationContext as App).appComponent.accountComponent().create()
            .inject(this)
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
        binding.user = appPrefs.user
        binding.editProfileButtonClicked = View.OnClickListener {
            val dir =
                CustomerAccountFragmentDirections.actionNavigationAccountToPersonalDetailsFragment()
            dir.onboarding = false
            findNavController().navigate(dir)
        }
        binding.registerButtonClicked = View.OnClickListener {
            requireActivity().launchActivity<BusinessProfileSetupScreen> {}
        }

    }

    private fun submitOptions() {
        val options = listOf(
            AccountOption(
                1,
                "Change City",
                "Your current city: ${appPrefs.user?.city?.name}",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(2, "My Reviews", "Reviews given by you", R.drawable.ic_post_like_filled),
            AccountOption(
                3,
                "My Suggestions",
                "Suggestions given by you",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(4, "Help", "Help and FAQs", R.drawable.ic_post_like_filled),
            AccountOption(
                10,
                "Rate this app",
                "Review app on Play Store",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(
                5,
                "Logout",
                "Logout from ${getString(R.string.app_name)}",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(
                -1,
                "Interests",
                "Logout from ${getString(R.string.app_name)}",
                R.drawable.ic_post_like_filled
            )
        )
        accountOptionsAdapter.submitList(options)
    }

    private val onItemSelected = { option: AccountOption ->
        Log.d(TAG, "${option.title} selected")
        when (option.id) {
            1 -> {
                val d =
                    CustomerAccountFragmentDirections.actionNavigationAccountToSelectCityFragment()
                findNavController().navigate(d)
            }
            2 -> {
                val d =
                    CustomerAccountFragmentDirections.actionNavigationCustomerAccountToMyReviewsFragment()
                findNavController().navigate(d)
            }
            3 -> {
                val d =
                    CustomerAccountFragmentDirections.actionNavigationCustomerAccountToMySuggestionsFragment()
                findNavController().navigate(d)
            }
            4 -> {
                val d =
                    CustomerAccountFragmentDirections.actionNavigationAccountToHelpFragment()
                findNavController().navigate(d)
            }
            5 -> {
                appPrefs.clearUserPrefs()
                launchActivity<SplashScreen> {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            10 -> {
                requireActivity().showAppInStore()
            }

            -1 -> {
                CustomerAccountFragmentDirections.actionNavigationCustomerAccountToSelectInterestsFragment()
                    .let {
                        findNavController().navigate(it)
                    }
            }
        }
    }
}
