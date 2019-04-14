package com.feedbacktower.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.feedbacktower.R
import com.feedbacktower.util.gone
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.fragment_account_selection_type.view.*

class AccountTypeSelectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_account_selection_type, container, false)
        v.radioGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.businessRadio -> {
                    v.businessFieldsLayout.visible()
                }
                R.id.customerRadio -> {
                    v.businessFieldsLayout.gone()
                }
            }
        }
        return v
    }


}
