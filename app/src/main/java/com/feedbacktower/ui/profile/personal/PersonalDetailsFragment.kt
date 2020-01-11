package com.feedbacktower.ui.profile.personal

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.data.models.User
import com.feedbacktower.databinding.FragmentPersonalDetailsBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.ui.utils.SpinnerDatePickerDialog
import com.feedbacktower.util.imageUriToFile
import com.feedbacktower.util.isEmailValid
import com.feedbacktower.util.permissions.PermissionManager
import com.feedbacktower.util.toUserProfileRound
import com.feedbacktower.utilities.compressor.Compressor
import com.feedbacktower.utilities.filepicker.FilePickerBuilder
import com.feedbacktower.utilities.filepicker.FilePickerConst
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.theartofdev.edmodo.cropper.CropImage
import org.jetbrains.anko.toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class PersonalDetailsFragment : BaseViewFragmentImpl(), PersonalDetailContract.View,
    SpinnerDatePickerDialog.OnDateSelectedListener {

    @Inject
    lateinit var presenter: PersonalDetailPresenter

    private lateinit var binding: FragmentPersonalDetailsBinding
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as App).appComponent.accountComponent().create().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPersonalDetailsBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        initUi()
        return binding.root
    }

    private fun initUi() {
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

            presenter.updateDetails(firstName, lastName, email, dob)
        }
        binding.onAttachClick = View.OnClickListener {
            if (PermissionManager.getInstance().readyToPickImage(requireContext()))
                pickImage()
            else {
                PermissionManager.getInstance().requestMediaPermission(this)
            }
        }
        PermissionManager.getInstance().requestMediaPermission(this)
        binding.user = presenter.user
        setDateText(presenter.user.dob)
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
                    val uri = File(paths[0]).toUri()
                    CropImage.activity(uri)
                        .setAspectRatio(1, 1)
                        .start(requireContext(), this)
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                data?.let {
                    val result = CropImage.getActivityResult(data)
                    if (resultCode == RESULT_OK) {
                        val resultUri = result.uri
                        requireActivity().imageUriToFile(resultUri).let { file ->
                            val fileToUpload = Compressor(requireActivity()).compressToFile(file)
                            presenter.uploadProfilePicture(fileToUpload)
                            Log.d(TAG, "Reduced: ${file.length() / 1024}KB to ${fileToUpload.length() / 1024}KB")
                        }
                    }
                }
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

    override fun onProfileUploaded(path: String?) {
        profileImage.toUserProfileRound(path)
        requireContext().toast("Uploaded successfully")
    }

    override fun onDetailsUpdated(firstName: String, lastName: String, email: String, dateOfBirth: String) {
        if (!args.onboarding) {
            findNavController().navigateUp()
        } else {
            PersonalDetailsFragmentDirections.actionPersonalDetailsFragmentToSelectCityFragmentOnB()
                .let {
                    findNavController().navigate(it)
                }
        }
    }

    override fun showProfileUploadProgress() {
        binding.showProfileUploadProgress = true
    }

    override fun hideProfileUploadProgress() {
        binding.showProfileUploadProgress = false
    }

    override fun showUpdateDetailsProgress() {
        binding.loading = true
    }

    override fun hideUpdateDetailsProgress() {
        binding.loading = false
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyView()
    }
}
