package com.feedbacktower.ui.profile.personal

import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.ui.base.BasePresenterImpl
import java.io.File

class PersonalDetailPresenter : BasePresenterImpl<PersonalDetailContract.View>(),
    PersonalDetailContract.Presenter {
    override fun updateDetails(firstName: String, lastName: String, email: String, dateOfBirth: String) {
        getView()?.showUpdateDetailsProgress()
        ProfileManager.getInstance()
            .updatePersonalDetails(firstName, lastName, email, dateOfBirth) { _, error ->
                getView()?.hideUpdateDetailsProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@updatePersonalDetails
                }
                getView()?.onDetailsUpdated(firstName, lastName, email, dateOfBirth)
            }
    }

    override fun uploadProfilePicture(file: File) {
        getView()?.showProfileUploadProgress()
        ProfileManager.getInstance()
            .uploadProfile(file) { _, error ->
                getView()?.hideProfileUploadProgress()
                if (error != null) {
                    getView()?.showNetworkError(error)
                    return@uploadProfile
                }
                getView()?.onProfileUploaded()
            }
    }

}