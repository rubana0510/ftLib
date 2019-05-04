package com.feedbacktower.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentPersonalDetailsBinding
import com.feedbacktower.fragments.utils.SpinnerDatePickerDialog
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import com.feedbacktower.util.isEmailValid
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class PersonalDetailsFragment : Fragment(), SpinnerDatePickerDialog.OnDateSelectedListener {
    private val TAG = "PersonalDetails"
    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var dobLayout: TextInputLayout
    private lateinit var continueButton: Button
    private lateinit var firstNameInput: TextInputEditText
    private lateinit var lastNameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var dobInput: TextInputEditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentPersonalDetailsBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentPersonalDetailsBinding) {
        firstNameLayout = binding.firstNameLayout
        lastNameLayout = binding.lastNameLayout
        emailLayout = binding.emailLayout
        dobLayout = binding.birthdateLayout

        firstNameInput = binding.firstNameInput
        lastNameInput = binding.lastNameInput
        emailInput = binding.emailInput
        dobInput = binding.birthdateInput
        dobInput.inputType = InputType.TYPE_NULL
        continueButton = binding.continueButton
        dobInput.setOnClickListener {
            val dialog = SpinnerDatePickerDialog.getInstance()
            dialog.setDateSelectListener(this@PersonalDetailsFragment)
            dialog.show(fragmentManager, "date_selector")
        }

        continueButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val dob = dobInput.text.toString().trim()
            if (!valid(firstName, lastName, email, dob))
                return@setOnClickListener

            updateDetails(firstName, lastName, email, dob)
        }

        binding.user = AppPrefs.getInstance(requireContext()).user
    }

    private fun updateDetails(firstName: String, lastName: String, email: String, dob: String) {
        showLoading()
        ProfileManager.getInstance()
            .updatePersonalDetails(firstName, lastName, email, dob)
            { response, error ->
                hideLoading()
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@updatePersonalDetails
                }

                PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToSelectCityFragment().let {
                    findNavController().navigate(it)
                }

            }
    }

    private fun hideLoading() {
        continueButton.enable()
    }

    private fun showLoading() {
        continueButton.disable()
    }

    private fun valid(firstName: String, lastName: String, email: String, dob: String): Boolean {
        firstNameLayout.error = null
        lastNameLayout.error = null
        emailLayout.error = null
        dobLayout.error = null
        return when {
            firstName.isEmpty() -> {
                firstNameLayout.error = "Enter valid first name"
                false
            }
            lastName.isEmpty() -> {
                lastNameLayout.error = "Enter valid last name"
                false
            }
            email.isEmpty() || !isEmailValid(email) -> {
                emailLayout.error = "Enter valid email id"
                false
            }
            dob.isEmpty() -> {
                dobLayout.error = "Enter valid DOB"
                false
            }
            else -> true
        }
    }

    private fun isValidDate(dob: String): Boolean {
        return false
    }

    private fun showDobPicker() {

    }

    override fun onDateSelect(dayOfMonth: Int, month: Int, year: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.YEAR, year)
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val date = sdf.format(cal.time)
        dobInput.setText(date)
    }
}
