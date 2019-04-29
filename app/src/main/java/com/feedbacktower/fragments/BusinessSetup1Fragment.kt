package com.feedbacktower.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.feedbacktower.databinding.FragmentBusinessSetup1Binding

class BusinessSetup1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentBusinessSetup1Binding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentBusinessSetup1Binding) {
        binding.continueButton.setOnClickListener {
            BusinessSetup1FragmentDirections.actionBusinessSetup1FragmentToBusinessSetup2Fragment().let {
                findNavController().navigate(it)
            }
        }
    }


}
