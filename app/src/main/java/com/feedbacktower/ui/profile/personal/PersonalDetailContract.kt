package com.feedbacktower.ui.profile.personal

import com.feedbacktower.ui.base.BasePresenter
import com.feedbacktower.ui.base.BaseView
import java.io.File

interface PersonalDetailContract {
    interface View : BaseView {
        fun onProfileUploaded(path: String?)
        fun onDetailsUpdated(firstName: String, lastName: String, email: String, dateOfBirth: String)
        fun showProfileUploadProgress()
        fun hideProfileUploadProgress()
        fun showUpdateDetailsProgress()
        fun hideUpdateDetailsProgress()
    }

    interface Presenter : BasePresenter<View> {
        fun updateDetails(firstName: String, lastName: String, email: String, dateOfBirth: String)
        fun uploadProfilePicture(file: File)
    }
}