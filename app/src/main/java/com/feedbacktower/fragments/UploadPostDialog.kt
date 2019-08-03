package com.feedbacktower.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.feedbacktower.R
import com.feedbacktower.callbacks.BottomSheetOnStateChanged
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_upload_post.view.*


class UploadPostDialog(val listener: Listener) : BottomSheetDialogFragment() {

    private val onStateChangedCallback = BottomSheetOnStateChanged { _, newState ->
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismiss()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val contentView = View.inflate(context, R.layout.dialog_upload_post, null)
        dialog.setContentView(contentView)
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior) {
            behavior.setBottomSheetCallback(onStateChangedCallback)
        }
        (contentView.parent as View).setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.transparent))
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