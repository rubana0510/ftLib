package com.feedbacktower.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import com.feedbacktower.R
import com.feedbacktower.ui.base.BaseViewBottomSheetDialogFragmentImpl
import kotlinx.android.synthetic.main.dialog_upload_post.view.*


class UploadPostDialog(val listener: Listener) : BaseViewBottomSheetDialogFragmentImpl() {

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val contentView = View.inflate(context, R.layout.dialog_upload_post, null)
        dialog.setContentView(contentView)
        initUI(contentView)
    }

    private fun initUI(contentView: View) {
        contentView.photo.setOnClickListener { listener.imageClick(); dismiss() }
        contentView.text.setOnClickListener { listener.textClick(); dismiss() }
        contentView.video.setOnClickListener { listener.videoClick(); dismiss() }
    }

    interface Listener {
        fun videoClick()
        fun imageClick()
        fun textClick()
    }

}