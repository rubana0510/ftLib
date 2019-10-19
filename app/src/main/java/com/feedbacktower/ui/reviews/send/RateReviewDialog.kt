package com.feedbacktower.ui.reviews.send

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.*
import com.feedbacktower.R
import com.feedbacktower.ui.business_details.BusinessDetailFragment
import com.feedbacktower.network.manager.ReviewsManager
import com.feedbacktower.ui.base.BaseViewBottomSheetDialogFragmentImpl
import com.feedbacktower.util.toRemarkText
import kotlinx.android.synthetic.main.dialog_rate_review.view.*


class RateReviewDialog(val listener: BusinessDetailFragment.UpdateListener?) : BaseViewBottomSheetDialogFragmentImpl() {
    private lateinit var ctx: Context
    private lateinit var closeButton: ImageButton
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewInput: EditText
    private lateinit var sendRatingButton: Button
    private lateinit var remarkText: TextView
    private lateinit var businessId: String

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        ctx = activity ?: return
        businessId = arguments?.getString("businessId")!!

        val contentView = View.inflate(context, R.layout.dialog_rate_review, null)
        dialog.setContentView(contentView)
        setUpBehaviour(contentView)
        initUI(contentView)
    }

    private fun initUI(contentView: View) {
        closeButton = contentView.closeButton
        sendRatingButton = contentView.sendRatingButton
        reviewInput = contentView.reviewInput
        remarkText = contentView.remarkText
        ratingBar = contentView.ratingBar


        closeButton.setOnClickListener { dismiss() }
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, stars, _ -> remarkText.text = stars.toRemarkText() }
        sendRatingButton.setOnClickListener { sendRatings() }
    }


    private fun sendRatings() {
        val noOfStars: Int = ratingBar.rating.toInt()
        val reviewSummary: String = reviewInput.text.toString()

        ReviewsManager.getInstance()
            .addReview(
                hashMapOf(
                    "businessId" to businessId,
                    "rating" to noOfStars,
                    "comment" to reviewSummary
                )
            ) { _, _ ->

                listener?.update()
                dismiss()

            }

    }

}