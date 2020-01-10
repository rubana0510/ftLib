package com.feedbacktower.ui.suggestions.send

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.ui.business_detail.BusinessDetailFragment
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewBottomSheetDialogFragmentImpl
import kotlinx.android.synthetic.main.dialog_send_suggestion.view.*
import org.jetbrains.anko.toast
import javax.inject.Inject


class SendSuggestionDialog(val listener: BusinessDetailFragment.UpdateListener?) :
    BaseViewBottomSheetDialogFragmentImpl(), SuggestionView {

    @Inject
    lateinit var presenter: SuggestionPresenter
    private lateinit var ctx: Context
    private lateinit var closeButton: ImageButton
    private lateinit var suggestionMessageInput: EditText
    private lateinit var sendSuggestionButton: Button
    private lateinit var businessId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as App).appComponent.suggestionComponent().create().inject(this)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        ctx = activity ?: return
        businessId = arguments?.getString("businessId")!!
        val contentView = View.inflate(context, R.layout.dialog_send_suggestion, null)
        dialog.setContentView(contentView)
        setUpBehaviour(contentView)
        initUI(contentView)
        presenter.attachView(this)
    }

    private fun initUI(contentView: View) {
        closeButton = contentView.closeButton
        sendSuggestionButton = contentView.sendSuggestionButton
        suggestionMessageInput = contentView.suggestionMessageInput

        closeButton.setOnClickListener { dismiss() }
        sendSuggestionButton.setOnClickListener { sendSuggestion() }
    }

    private fun sendSuggestion() {
        val suggestionMessage: String = suggestionMessageInput.text.toString()
        if (suggestionMessage.isEmpty()) return
        presenter.sendSuggestion(businessId, suggestionMessage)
    }

    override fun showProgress() {
        super.showProgress()
        isCancelable = false
    }

    override fun dismissProgress() {
        super.dismissProgress()
        isCancelable = true
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }

    override fun onSentSuccess() {
        dismiss()
    }

    override fun onDestroy() {
        presenter.destroView()
        super.onDestroy()
    }

}