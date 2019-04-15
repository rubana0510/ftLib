package com.feedbacktower.fragments.business


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.feedbacktower.databinding.FragmentReviewsBinding


class ReviewsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentReviewsBinding.inflate(inflater, container, false)
        return  binding.root
    }


}
