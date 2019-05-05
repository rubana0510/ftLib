package com.feedbacktower.fragments.business

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.R
import com.feedbacktower.adapters.AccountOptionsAdapter
import com.feedbacktower.adapters.CountAdapter
import com.feedbacktower.data.local.models.AccountOption
import com.feedbacktower.data.local.models.Count
import com.feedbacktower.databinding.FragmentBusinessAccountBinding


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

        binding.url = "https://via.placeholder.com/150"
    }

    private fun submitOptions() {
        val options = listOf(
            AccountOption(1, "Subscription", "XXX days left", R.drawable.ic_post_like_filled),
            AccountOption(2, "My Reviews", "20 reviews", R.drawable.ic_post_like_filled),
            AccountOption(3, "My Suggestions", "10 suggestions", R.drawable.ic_post_like_filled),
            AccountOption(4, "Help", "Help and FAQs", R.drawable.ic_post_like_filled),
            AccountOption(5, "Logout", "Logout from ${getString(R.string.app_name)}", R.drawable.ic_post_like_filled)
        )
        accountOptionsAdapter.submitList(options)
    }

    private val onItemSelected = { option: AccountOption ->
        Log.d(TAG, "${option.title} selected")
    }

    private fun submitCounts() {
        val counts = listOf(
            Count(1, "4.2", "Average Ratings"),
            Count(2, "3", "Rank"),
            Count(3, "1099", "Total Reviews"),
            Count(4, "102", "Total Suggestions")
        )
        countAdapter.submitList(counts)
    }


}
