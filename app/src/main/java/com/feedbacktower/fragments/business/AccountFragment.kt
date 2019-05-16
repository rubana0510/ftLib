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
import com.feedbacktower.ui.ProfileSetupScreen
import com.feedbacktower.ui.SplashScreen
import com.feedbacktower.ui.qrtransfer.ReceiverActivity
import com.feedbacktower.ui.qrtransfer.SenderActivity
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.noZero


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
        countAdapter = CountAdapter()
        counterGridView.adapter = countAdapter
        submitCounts()

        //setup options list
        accountOptionsView.setHasFixedSize(true)
        accountOptionsAdapter = AccountOptionsAdapter(onItemSelected)
        accountOptionsView.adapter = accountOptionsAdapter
        submitOptions()


        binding.user = AppPrefs.getInstance(requireContext()).user!!
        binding.editProfileButtonClicked = View.OnClickListener {
            requireActivity().launchActivity<ProfileSetupScreen>()
        }
        binding.onScanClicked = View.OnClickListener {
            requireActivity().launchActivity<ReceiverActivity>()
        }

    }

    private fun submitOptions() {
        val business = AppPrefs.getInstance(requireContext()).user?.business!!
        val options = listOf(
            AccountOption(1, "Subscription", "365 days left", R.drawable.ic_post_like_filled),
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
                val d = AccountFragmentDirections.actionNavigationAccountToMyReviewsFragment()
                findNavController().navigate(d)
            }
            3 -> {
                val d = AccountFragmentDirections.actionNavigationAccountToMySuggestionsFragment()
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

    private fun submitCounts() {
        val business = AppPrefs.getInstance(requireContext()).user?.business!!
        val counts = listOf(
            Count(1, business.rank.noZero(), "Rank"),
            Count(2, "${business.averageRatings}", "Ratings"),
            Count(3, "${business.totalReviews}", "Reviews"),
            Count(4, "${business.totalSuggestions}", "Suggestions")
        )
        countAdapter.submitList(counts)
    }


}
