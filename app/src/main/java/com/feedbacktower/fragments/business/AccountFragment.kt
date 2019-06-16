package com.feedbacktower.fragments.business

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.R
import com.feedbacktower.adapters.AccountOptionsAdapter
import com.feedbacktower.adapters.CountAdapter
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.local.models.AccountOption
import com.feedbacktower.data.local.models.Count
import com.feedbacktower.databinding.FragmentBusinessAccountBinding
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.ProfileSetupScreen
import com.feedbacktower.ui.SplashScreen
import com.feedbacktower.ui.map.MapScreen
import com.feedbacktower.ui.myplan.MyPlanScreen
import com.feedbacktower.ui.qrtransfer.ReceiverActivity
import com.feedbacktower.ui.qrtransfer.SenderActivity
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.noZero
import com.feedbacktower.util.showAppInStore
import kotlinx.android.synthetic.main.fragment_business_account.*
import org.jetbrains.anko.toast


class AccountFragment : Fragment() {

    private val TAG = "AccountFragment"
    private lateinit var counterGridView: RecyclerView
    private lateinit var accountOptionsView: RecyclerView

    private lateinit var accountOptionsAdapter: AccountOptionsAdapter
    private lateinit var countAdapter: CountAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentBusinessAccountBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).supportActionBar?.show()
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentBusinessAccountBinding) {
        counterGridView = binding.counterGridView
        accountOptionsView = binding.accountOptionsView

        //setup counter grid list
        counterGridView.setHasFixedSize(true)
        countAdapter = CountAdapter(onCountClick)
        counterGridView.adapter = countAdapter
        submitCounts()

        //setup options list
        accountOptionsView.setHasFixedSize(true)
        accountOptionsAdapter = AccountOptionsAdapter(onItemSelected)
        accountOptionsView.adapter = accountOptionsAdapter
        submitOptions()

        binding.availabilitySwitch.setOnClickListener {
            val state = binding.availabilitySwitch.isChecked
            binding.updatingStatus = true
            ProfileManager.getInstance()
                .changeBusinessAvailability(state) { _, error ->
                    binding.updatingStatus = false
                    if (error != null) {
                        requireContext().toast(error.message ?: getString(R.string.default_err_message))
                        binding.availabilitySwitch.isChecked = !state
                        return@changeBusinessAvailability
                    }
                    binding.availabilitySwitch.isChecked = state
                    AppPrefs.getInstance(requireContext()).apply {
                        user?.apply {
                            business = business?.apply {
                                visible = state
                            }
                        }
                    }
                }
        }
        binding.business = AppPrefs.getInstance(requireContext()).user?.business!!
        binding.editProfileButtonClicked = View.OnClickListener {
            val dir = AccountFragmentDirections.actionNavigationAccountToEditBusinessFragment()
            findNavController().navigate(dir)
        }
        binding.onScanClicked = View.OnClickListener {
            requireActivity().launchActivity<ReceiverActivity>()
        }

    }

    override fun onResume() {
        super.onResume()
        fetchBusinessDetails()
    }

    private fun fetchBusinessDetails() {
        ProfileManager.getInstance()
            .getMyBusiness { response, error ->
                if (error == null) {
                    AppPrefs.getInstance(requireContext()).apply {
                        user = user?.apply {
                            business = response?.business
                        }
                    }
                    submitOptions()
                    Log.d(TAG, "Details updated")
                }
            }
    }

    private fun submitOptions() {
        val business = AppPrefs.getInstance(requireContext()).user?.business!!
        val options = listOf(
            AccountOption(
                9,
                "Change City",
                "Your current city: ${AppPrefs.getInstance(requireContext()).user?.city?.name}",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(
                6,
                "Wallet Transactions",
                "Wallet balance ₹${business.walletAmount}, Discount Amount: ₹${business.discountAmount}",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(2, "My Reviews", "Reviews given by you", R.drawable.ic_post_like_filled),
            AccountOption(3, "My Suggestions", "Suggestions given by you", R.drawable.ic_post_like_filled),
            AccountOption(8, "My Posts", "Manage your posts", R.drawable.ic_post_like_filled),
            AccountOption(1, "Subscription", "Current plan", R.drawable.ic_post_like_filled),
            AccountOption(4, "Help", "Help and FAQs", R.drawable.ic_post_like_filled),
            AccountOption(10, "Rate and Review", "Review app on Play Store", R.drawable.ic_post_like_filled),
            AccountOption(7, "Settings", "Notifications, Location", R.drawable.ic_post_like_filled),
            AccountOption(5, "Logout", "Logout from ${getString(R.string.app_name)}", R.drawable.ic_post_like_filled)
        )
        accountOptionsAdapter.submitList(options)
    }

    private val onCountClick = { count: Count ->
        if (count.id == 3) {
            val d = AccountFragmentDirections.actionNavigationAccountToNavigationReviewFragment()
            findNavController().navigate(d)
        } else if (count.id == 4) {
            val d = AccountFragmentDirections.actionNavigationAccountToNavigationSuggestionFragment()
            findNavController().navigate(d)
        }
    }

    private val onItemSelected = { option: AccountOption ->
        Log.d(TAG, "${option.title} selected")
        when (option.id) {
            1 -> {
                requireActivity().launchActivity<MyPlanScreen>()
            }
            2 -> {
                val d = AccountFragmentDirections.actionNavigationAccountToMyReviewsFragment()
                findNavController().navigate(d)
            }
            3 -> {
                val d = AccountFragmentDirections.actionNavigationAccountToMySuggestionsFragment()
                findNavController().navigate(d)
            }
            4 -> {
                val d = AccountFragmentDirections.actionNavigationAccountToHelpFragment()
                findNavController().navigate(d)
            }
            5 -> {
                AppPrefs.getInstance(requireContext()).authToken = null
                AppPrefs.getInstance(requireContext()).user = null
                requireActivity().launchActivity<SplashScreen> {

                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            6 -> {
                val d = AccountFragmentDirections.actionNavigationAccountToWalletTransactionFragment()
                findNavController().navigate(d)
            }
            7 -> {
                val d = AccountFragmentDirections.actionNavigationAccountToBusinessSettingsFragment()
                findNavController().navigate(d)
            }
            8 -> {
                val d = AccountFragmentDirections.actionNavigationAccountToMyPostsFragment()
                findNavController().navigate(d)
            }
            9 -> {
                val d = AccountFragmentDirections.actionNavigationAccountToSelectCityFragment()
                findNavController().navigate(d)
            }
            10 -> {
                requireActivity().showAppInStore()
            }
        }
    }

    private fun submitCounts() {
        val business = AppPrefs.getInstance(requireContext()).user?.business!!
        val counts = listOf(
            Count(1, business.rank.noZero(), "Rank"),
            Count(2, "${business.avgRating}", "Ratings"),
            Count(3, "${business.totalReviews}", "Reviews"),
            Count(4, "${business.totalSuggestions}", "Suggestions")
        )
        countAdapter.submitList(counts)
    }


}
