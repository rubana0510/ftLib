package com.feedbacktower.fragments.business


import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.data.models.BusinessCategory
import com.feedbacktower.databinding.FragmentBusinessSetup1Binding
import com.feedbacktower.fragments.SelectCategoryFragment
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.toast

class BusinessSetup1Fragment : Fragment() {
    private lateinit var nameLayout: TextInputLayout
    private lateinit var regLayout: TextInputLayout
    private lateinit var categoryIdLayout: TextInputLayout
    private lateinit var continueButton: Button
    private lateinit var nameInput: TextInputEditText
    private lateinit var regNoInput: TextInputEditText
    private lateinit var catIdInput: TextInputEditText

    private var selectedCatId: String? = null

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

        binding.business = AppPrefs.getInstance(requireContext()).user?.business

        nameLayout = binding.businessNameLayout
        regLayout = binding.estRegNoLayout
        categoryIdLayout = binding.businessCatLayout

        nameInput = binding.businessNameInput
        regNoInput = binding.estRegNoInput
        catIdInput = binding.businessCatInput
        catIdInput.inputType = InputType.TYPE_NULL
        continueButton = binding.continueButton

        binding.onContinueClick = View.OnClickListener {
            if (valid(nameInput.text.toString().trim(), regNoInput.text.toString().trim(), selectedCatId)) {
                updateDetails(
                    nameInput.text.toString().trim(),
                    regNoInput.text.toString().trim()
                )
            }
        }

        catIdInput.setOnClickListener {
            val fragment = SelectCategoryFragment.getInstance()
            fragment.listener = object : SelectCategoryFragment.CategorySelectListener {
                override fun onSelect(category: BusinessCategory) {
                    catIdInput.setText(category.name)
                    selectedCatId = category.id
                }
            }
            fragment.show(fragmentManager, "select_category")
        }
    }

    private fun hideLoading() {
        continueButton.enable()
    }

    private fun showLoading() {
        continueButton.disable()
    }

    private fun valid(name: String, regNo: String, category: String?): Boolean {
        nameLayout.error = null
        regLayout.error = null
        categoryIdLayout.error = null

        return when {
            name.isEmpty() -> {
                nameLayout.error = "Enter valid business name"
                false
            }
            regNo.isEmpty() -> {
                regLayout.error = "Enter valid registration number"
                false
            }
            category.isNullOrEmpty() -> {
                categoryIdLayout.error = "Select valid category"
                false
            }
            else -> true
        }
    }

    private fun navigateNext() {
        BusinessSetup1FragmentDirections.actionBusinessSetup1FragmentToBusinessSetup2Fragment()
            .let {
                findNavController().navigate(it)
            }
    }

    private fun updateDetails(
        name: String,
        regNo: String
    ) {
        showLoading()
        ProfileManager.getInstance()
            .updateBusinessBasicDetails(name, regNo, selectedCatId!!)
            { response, error ->
                hideLoading()
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@updateBusinessBasicDetails
                }
                navigateNext()
            }
    }

}
