package com.feedbacktower.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.feedbacktower.BusinessMainActivity
import com.feedbacktower.databinding.FragmentBusinessSetup2Binding
import com.feedbacktower.util.launchActivity


class BusinessSetup2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentBusinessSetup2Binding.inflate(inflater, container, false)

        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentBusinessSetup2Binding) {
        binding.continueButton.setOnClickListener {
            activity?.launchActivity<BusinessMainActivity>()
        }
    }

}
