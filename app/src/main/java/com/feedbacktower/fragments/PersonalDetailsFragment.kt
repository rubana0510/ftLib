package com.feedbacktower.fragments
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import kotlinx.android.synthetic.main.fragment_personal_details.view.*

class PersonalDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_personal_details, container, false)
        v.continueButton.setOnClickListener {
            val direction = PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToAddressDetailsFragment()
            findNavController().navigate(direction)
        }
        return v
    }


}
