package com.feedbacktower.ui.ads


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.BuildConfig
import com.feedbacktower.databinding.FragmentAdDetailBinding


class AdDetailFragment : Fragment() {
    private val args: AdDetailFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //requireActivity().title  = args.ad.business.name
        val binding = FragmentAdDetailBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentAdDetailBinding) {
        requireActivity().title = args.ad.business.name
        binding.ad = args.ad
        binding.openInBrowser = View.OnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(args.ad.link)
                )
            )
        }
        binding.showBusinessDetails = View.OnClickListener {
            AdDetailFragmentDirections.actionAdDetailFragmentToBusinessDetailsActivity(args.ad.business.id).let {
                findNavController().navigate(it)
            }
        }
    }
}