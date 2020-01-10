package com.feedbacktower.ui.reviews.send

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.business_detail.BusinessDetailFragment
import com.feedbacktower.ui.base.BaseViewBottomSheetDialogFragmentImpl
import com.feedbacktower.util.toRemarkText
import kotlinx.android.synthetic.main.dialog_rate_review.view.*
import org.jetbrains.anko.toast
import javax.inject.Inject


class RateReviewDialog(val listener: BusinessDetailFragment.UpdateListener?) : BaseViewBottomSheetDialogFragmentImpl(), RateReviewView {
    @Inject
    lateinit var presenter: RateReviewPresenter
    private lateinit var ctx: Context
    private lateinit var closeButton: ImageButton
    private lateinit var ratingBar: RatingBar
    private lateinit var reviewInput: EditText
    private lateinit var sendRatingButton: Button
    private lateinit var remarkText: TextView
    private lateinit var businessId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as App).appComponent.reviewComponent().create().inject(this)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        ctx = activity ?: return
        businessId = arguments?.getString("businessId")!!

        val contentView = View.inflate(context, R.layout.dialog_rate_review, null)
        dialog.setContentView(contentView)
        setUpBehaviour(contentView)
        initUI(contentView)
        presenter.attachView(this)
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

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }

    private fun sendRatings() {
        val noOfStars: Int = ratingBar.rating.toInt()
        val reviewSummary: String = reviewInput.text.toString()
        presenter.rate(businessId, noOfStars, reviewSummary)
    }

    override fun onRateSuccess() {
        listener?.update()
        dismiss()
    }

    override fun onDestroy() {
        presenter.destroView()
        super.onDestroy()
    }

}