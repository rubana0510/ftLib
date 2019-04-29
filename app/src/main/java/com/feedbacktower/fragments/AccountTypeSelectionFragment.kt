package com.feedbacktower.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.feedbacktower.R
import com.feedbacktower.ui.CustomerMainActivity
import com.feedbacktower.util.gone
import com.feedbacktower.util.launchActivity
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.fragment_account_selection_type.view.*

class AccountTypeSelectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_account_selection_type, container, false)
        v.customerButton.setOnClickListener {
            activity?.launchActivity<CustomerMainActivity>()
        }

        v.businessButton.setOnClickListener {
            AccountTypeSelectionFragmentDirections.actionAccountTypeSelectionFragmentToBusinessSetup1Fragment().let {
                findNavController().navigate(it)
            }
        }
        return v
    }


}
