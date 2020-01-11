package com.feedbacktower.ui.profile.personal

import android.util.Log
import com.feedbacktower.data.ApplicationPreferences
import com.feedbacktower.data.models.User
import com.feedbacktower.network.service.ApiService
import com.feedbacktower.network.utils.awaitNetworkRequest
import com.feedbacktower.ui.base.BasePresenterImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class PersonalDetailPresenter
@Inject constructor(
    private val apiService: ApiService,
    private val appPrefs: ApplicationPreferences
) : BasePresenterImpl<PersonalDetailContract.View>(),
    PersonalDetailContract.Presenter {

    val user: User
        get() = appPrefs.user?:throw IllegalStateException("User cannot be null")

    override fun updateDetails(firstName: String, lastName: String, email: String, dateOfBirth: String) {
        GlobalScope.launch(Dispatchers.Main) {
            view?.showUpdateDetailsProgress()
            val response = apiService.updatePersonalDetailsAsync(
                hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "dob" to dateOfBirth,
                    "emailId" to email
                )
            ).awaitNetworkRequest()
            view?.hideUpdateDetailsProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            appPrefs.apply {
                user = user?.apply {
                    this.firstName = firstName
                    this.lastName = lastName
                    this.emailId = email
                    this.dob = dateOfBirth
                }
            }
            view?.onDetailsUpdated(firstName, lastName, email, dateOfBirth)
        }
    }

    override fun uploadProfilePicture(file: File) {
        if (!file.exists()) {
            Log.e("PerPresenter", "uploadImages: FileNotFound")
            return
        }
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        val filePart = MultipartBody.Part.createFormData("avatar", file.name, requestBody)
        GlobalScope.launch(Dispatchers.Main) {
            view?.showProfileUploadProgress()
            val response = apiService.uploadProfileAsync(
                filePart
            ).awaitNetworkRequest()
            view?.hideProfileUploadProgress()
            if (response.error != null) {
                view?.showNetworkError(response.error)
                return@launch
            }
            appPrefs.setValue("PROFILE_LAST_UPDATED", System.currentTimeMillis().toString())
            view?.onProfileUploaded(appPrefs.user?.id)
        }
    }

}