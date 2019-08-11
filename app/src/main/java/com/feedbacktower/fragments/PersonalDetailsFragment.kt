package com.feedbacktower.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.loader.content.CursorLoader
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentPersonalDetailsBinding
import com.feedbacktower.fragments.utils.SpinnerDatePickerDialog
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.*
import com.feedbacktower.utilities.Glide4Engine
import com.feedbacktower.utilities.filepicker.FilePickerBuilder
import com.feedbacktower.utilities.filepicker.FilePickerConst
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PersonalDetailsFragment : Fragment(), SpinnerDatePickerDialog.OnDateSelectedListener {
    private val TAG = "PersonalDetails"
    private val PERMISSION_CODE = 399
    private val REQUEST_CODE_CHOOSE_IMAGE = 1011
    private val CAPTURE_PHOTO_CODE = 1011
    private lateinit var firstNameLayout: TextInputLayout
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var emailLayout: TextInputLayout
    private lateinit var dobLayout: TextInputLayout
    private lateinit var continueButton: Button
    private lateinit var firstNameInput: TextInputEditText
    private lateinit var lastNameInput: TextInputEditText
    private lateinit var emailInput: TextInputEditText
    private lateinit var dobInput: TextInputEditText
    private val args: PersonalDetailsFragmentArgs by navArgs()
    private var lastImagePath: String? = null
    private lateinit var profileImage: ImageView
    private var dateSelected: String? = null
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
        profileImage = binding.profileImage

        dobInput.setOnClickListener {
            val dialog = SpinnerDatePickerDialog.getInstance()
            dialog.setDateSelectListener(this@PersonalDetailsFragment)
            dialog.show(fragmentManager!!, "date_selector")
        }

        continueButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim()
            val lastName = lastNameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val dob = dobInput.text.toString().trim()
            if (!valid(firstName, lastName, email, dob))
                return@setOnClickListener

            updateDetails(binding, firstName, lastName, email, dob)
        }
        binding.onAttachClick = View.OnClickListener {
            if (PermissionManager.getInstance().readyToPickImage(requireContext()))
                pickImage()
            else {
                PermissionManager.getInstance().requestMediaPermission(this)
            }
        }
        PermissionManager.getInstance().requestMediaPermission(this)
        val user = AppPrefs.getInstance(requireContext()).user
        binding.user = user
        if (user?.dob == null) {
            dobInput.setText("Select Date")
        } else {
            setDateText(user.dob)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    var paths: ArrayList<String> = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                    if (paths.size < 1) {
                        requireContext().toast("No image selected")
                        return
                    }
                    uploadProfile(File(paths[0]).toUri())
                }
            }
        }
    }

    private fun uploadProfile(uri: Uri?) {
        val file = activity?.uriToFile(uri!!)
        ProfileManager.getInstance()
            .uploadProfile(file!!) { _, error ->
                if (error == null) {
                    Glide.with(profileImage.context)
                        .load(uri)
                        .apply(RequestOptions().circleCrop())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(profileImage)
                    requireContext().toast("Uploaded successfully")
                } else {
                    requireContext().toast(error.message ?: "Error")
                }
            }
    }

    private fun pickImage() {
        FilePickerBuilder.instance.setMaxCount(1)
            .setActivityTheme(R.style.AppTheme_NoActionBar)
            .pickPhoto(this)
    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    pickImage()
                } else {
                    requireContext().toast("Grant permission to show attach images and capture photo")
                }
            }
        }
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun updateDetails(binding: FragmentPersonalDetailsBinding, firstName: String, lastName: String, email: String, dob: String) {
        binding.loading = true
        ProfileManager.getInstance()
            .updatePersonalDetails(firstName, lastName, email, dateSelected ?: "")
            { response, error ->
                binding.loading = false
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@updatePersonalDetails
                }

                AppPrefs.getInstance(requireContext()).apply {
                    user = user?.apply {
                        this.firstName = firstName
                        this.lastName = lastName
                        this.emailId = email
                        this.dob = dateSelected ?: ""
                    }
                }
                if (!args.onboarding) {
                    findNavController().navigateUp()
                } else {
                    PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToSelectCityFragmentOnB().let {
                        findNavController().navigate(it)
                    }
                }
            }
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
            !isEmailValid(email) -> {
                emailLayout.error = "Enter valid email id"
                false
            }
            dateSelected == null -> {
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
        // val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val sqldf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        //val date = sdf.format(cal.time)
        dateSelected = sqldf.format(cal.time)
        Log.d(TAG, "Selected Date: $dateSelected")
        dobInput.setText(dateSelected)
    }

    private fun setDateText(date: String) {
        val sqldf = SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH)
        //val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        dateSelected = date//sdf.format(sqldf.parse(date).time)
    }
}
