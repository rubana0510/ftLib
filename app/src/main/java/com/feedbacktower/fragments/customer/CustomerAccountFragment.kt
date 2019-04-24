package com.feedbacktower.fragments.customer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.R
import com.feedbacktower.adapters.AccountOptionsAdapter
import com.feedbacktower.data.local.models.AccountOption
import com.feedbacktower.databinding.FragmentCustomerAccountBinding


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

        binding.url = "https://via.placeholder.com/150"
    }

    private fun submitOptions() {
        val options = listOf(
            AccountOption(2, "My Reviews", "20 reviews", R.drawable.ic_post_like_filled),
            AccountOption(3, "My Suggestions", "10 suggestions", R.drawable.ic_post_like_filled),
            AccountOption(4, "Help", "Help and FAQs", R.drawable.ic_post_like_filled),
            AccountOption(5, "Logout", "Logout from ${R.string.app_name}", R.drawable.ic_post_like_filled)
        )
        accountOptionsAdapter.submitList(options)
    }

    private val onItemSelected = { option: AccountOption ->
        Log.d(TAG, "${option.title} selected")
    }

}
