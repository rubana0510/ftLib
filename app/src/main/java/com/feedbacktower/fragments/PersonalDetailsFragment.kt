package com.feedbacktower.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.loader.content.CursorLoader
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.databinding.FragmentPersonalDetailsBinding
import com.feedbacktower.fragments.utils.SpinnerDatePickerDialog
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import com.feedbacktower.util.isEmailValid
import com.feedbacktower.utilities.ImageEditHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import org.jetbrains.anko.toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PersonalDetailsFragment : Fragment(), SpinnerDatePickerDialog.OnDateSelectedListener {
    private val TAG = "PersonalDetails"
    private val PERMISSION_CODE = 399
    private val PICK_IMAGE_CODE = 1011
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

    private var lastImagePath: String? = null
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
        binding.onAttachClick = View.OnClickListener {
            //open picker
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !allPermissionsGranted()) {
                requestMediaPermission()
                return@OnClickListener
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Choose one")
                .setItems(arrayOf("CAMERA", "GALLERY")) { _, pos ->
                    if (pos == 0) {
                        startActivityForResult(
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            , PICK_IMAGE_CODE
                        )
                    } else {
                        takePicture()
                    }
                }.show()
        }
        binding.user = AppPrefs.getInstance(requireContext()).user
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            var photoURI: Uri? = null
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
                photoURI = FileProvider.getUriForFile(
                    requireActivity(),
                    getString(R.string.file_provider_authority),
                    photoFile!!
                )
            } catch (ex: IOException) {
                Log.e("TakePicture", ex.message)
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, CAPTURE_PHOTO_CODE)
        }
    }

    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val image: File = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        val mCurrentPhotoPath: String = image.absolutePath
        Log.d(TAG, "mCurrentPhotoPath: " + mCurrentPhotoPath)
        lastImagePath = mCurrentPhotoPath
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_CODE) {
                val selectedImage: Uri? = data?.data
                val path: String = getPath(selectedImage!!)
                //upload photo
                //confirmUpload(path)
                ImageEditHelper.openCropper(requireContext(), this, Uri.parse(path))
            } else if (requestCode == CAPTURE_PHOTO_CODE) {
                //upload photo
                //confirmUpload(lastImagePath)
                ImageEditHelper.openCropper(requireContext(), this, Uri.parse(lastImagePath))
            }
        }
    }

    private fun confirmUpload(lastImagePath: String?) {
        if (lastImagePath == null) {
            requireContext().toast("No image found")
            return
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Set as your profile?")
            .setMessage("Image will be set as you profile picture.")
            .setPositiveButton("OKAY", { dialogInterface, i ->
                //upload image
            })
            .setNegativeButton("CANCEL", null)
            .show()
    }

    fun getPath(contentUri: Uri): String {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(requireContext(), contentUri, proj, null, null, null)
        val cursor = loader.loadInBackground()!!
        val colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result = cursor.getString(colIndex)
        cursor.close()
        return result
    }

    private fun requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !allPermissionsGranted())
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

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
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
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

                AppPrefs.getInstance(requireContext()).apply {
                    user = user?.apply {
                        this.firstName = firstName
                        this.lastName = lastName
                        this.emailId = email
                        this.dob = dob
                    }
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
