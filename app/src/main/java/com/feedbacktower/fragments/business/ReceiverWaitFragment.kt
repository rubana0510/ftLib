package com.feedbacktower.fragments.business


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.feedbacktower.R
import com.feedbacktower.databinding.FragmentReciverWaitBinding


class ReceiverWaitFragment : Fragment() {

    private lateinit var binding: FragmentReciverWaitBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReciverWaitBinding.inflate(inflater, container, false)
        return  binding.root
    }


}
