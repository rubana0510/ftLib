package com.feedbacktower.ui.account


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.adapters.AccountOptionsAdapter
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.local.models.AccountOption
import com.feedbacktower.databinding.FragmentEditBusinessBinding
import javax.inject.Inject


class EditBusinessFragment : Fragment() {
    @Inject
    lateinit var appPrefs: ApplicationPreferences

    private lateinit var binding: FragmentEditBusinessBinding
    private lateinit var accountOptionsView: RecyclerView
    private lateinit var accountOptionsAdapter: AccountOptionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity().applicationContext as App).appComponent.accountComponent().create().inject(this)
        binding = FragmentEditBusinessBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    private fun initUi() {
        accountOptionsView = binding.accountOptionsView
        accountOptionsView.setHasFixedSize(true)
        accountOptionsAdapter = AccountOptionsAdapter(onItemSelected)
        accountOptionsView.adapter = accountOptionsAdapter
        submitOptions()
    }

    private fun submitOptions() {
        val options = listOf(
            AccountOption(1, "Personal Details", "Edit your name, email", R.drawable.ic_post_like_filled),
            AccountOption(2, "Business Details", "Edit business name, reg. no", R.drawable.ic_post_like_filled),
            AccountOption(3, "Business Address", "Edit business address", R.drawable.ic_post_like_filled),
            AccountOption(
                5,
                "Business City",
                "Your current city: ${appPrefs.user?.business?.city?.name}",
                R.drawable.ic_post_like_filled
            ),
            AccountOption(4, "Location On Map", "Change your permanent location", R.drawable.ic_post_like_filled)
        )
        accountOptionsAdapter.submitList(options)
    }

    private val onItemSelected = { option: AccountOption ->

        when (option.id) {
            1 -> {
                EditBusinessFragmentDirections.actionEditBusinessFragmentToPersonalDetailsFragment().let {
                    findNavController().navigate(it)
                }
            }
            2 -> {
                EditBusinessFragmentDirections.actionEditBusinessFragmentToBusinessSetup1FragmentOnB().let {
                    findNavController().navigate(it)
                }
            }
            3 -> {
                EditBusinessFragmentDirections.actionEditBusinessFragmentToBusinessSetup2FragmentOnB().let {
                    findNavController().navigate(it)
                }
            }
            4 -> {
                EditBusinessFragmentDirections.actionEditBusinessFragmentToPointOnMapFragmentOnB().let {
                    findNavController().navigate(it)
                }
            }
            5 -> {
                EditBusinessFragmentDirections.actionEditBusinessFragmentToSelectBusinessCityFragment().let {
                    findNavController().navigate(it)
                }
            }
        }
    }
}
