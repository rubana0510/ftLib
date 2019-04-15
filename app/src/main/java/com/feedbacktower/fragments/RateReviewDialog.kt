package com.feedbacktower.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.feedbacktower.R
import com.feedbacktower.callbacks.BottomSheetOnStateChanged
import com.feedbacktower.util.toRemarkText
import kotlinx.android.synthetic.main.dialog_rate_review.view.*
import org.jetbrains.anko.toast


class RateReviewDialog : BottomSheetDialogFragment() {

    private val onStateChangedCallback = BottomSheetOnStateChanged { _, newState ->
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismiss()
        }
    }

    private lateinit var ctx: Context
    private lateinit var closeButton: ImageButton
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewInput: EditText
    private lateinit var sendRatingButton: Button
    private lateinit var remarkText: TextView

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        ctx = activity ?: return
        val contentView = View.inflate(context, R.layout.dialog_rate_review, null)
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
        closeButton = contentView.closeButton
        sendRatingButton = contentView.sendRatingButton
        reviewInput = contentView.reviewInput
        remarkText = contentView.remarkText


        closeButton.setOnClickListener { dialog.dismiss() }
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, stars, _ -> remarkText.text = stars.toRemarkText() }
        sendRatingButton.setOnClickListener { sendRatings() }
    }

    private fun sendRatings() {
        val noOfStars: Int = ratingBar.numStars
        val reviewSummary: String = reviewInput.text.toString()
        ctx.toast("Sending ratings..$noOfStars stars with \"$reviewSummary\"")

        dialog.dismiss()
    }

}