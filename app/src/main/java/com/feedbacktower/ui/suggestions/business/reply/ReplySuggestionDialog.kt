package com.feedbacktower.ui.suggestions.business.reply

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.DialogReplySuggestionBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewBottomSheetDialogFragmentImpl
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import kotlinx.android.synthetic.main.dialog_reply_suggestion.view.*
import org.jetbrains.anko.toast
import javax.inject.Inject


class ReplySuggestionDialog : BaseViewBottomSheetDialogFragmentImpl(), ReplySuggestionContract.View {


    companion object {
        val TAG = "ReplySuggestion"
        fun getInstance(suggestion: Suggestion): ReplySuggestionDialog {
            return ReplySuggestionDialog().apply {
                arguments = Bundle().apply {
                    putSerializable("SUGGESTION", suggestion)
                }
            }
        }
    }

    @Inject
    lateinit var presenter: ReplySuggestionPresenter
    private lateinit var replyMessageInput: EditText
    private lateinit var sendReplyButton: Button
    private lateinit var suggestion: Suggestion
    var listener: ReplyCancelListener? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        (requireActivity().applicationContext as App).appComponent.suggestionComponent().create().inject(this)


        presenter.attachView(this)
        suggestion = arguments?.getSerializable("SUGGESTION") as? Suggestion
            ?: throw IllegalArgumentException("Wrong suggestion args")

        val contentView = View.inflate(context, R.layout.dialog_reply_suggestion, null)
        val binding = DialogReplySuggestionBinding.bind(contentView)
        binding.suggestion = suggestion
        dialog.setContentView(contentView)
        setUpBehaviour(contentView)
        initUI(contentView)
    }

    private fun initUI(contentView: View) {
        sendReplyButton = contentView.sendReplyButton
        replyMessageInput = contentView.replyMessageInput
        sendReplyButton.setOnClickListener { sendSuggestionReply() }
    }

    private fun showLoading() {
        sendReplyButton.text = "PLEASE WAIT..."
        sendReplyButton.disable()
        isCancelable = false
    }

    private fun hideLoading() {
        sendReplyButton.text = "REPLY"
        sendReplyButton.enable()
        isCancelable = true
    }

    private fun sendSuggestionReply() {
        val replyMessage: String = replyMessageInput.text.toString()
        if (replyMessage.isEmpty()) return

        presenter.reply(suggestion.id, replyMessage)
    }


    override fun showProgress() {
        super.showProgress()
        showLoading()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        hideLoading()
    }

    override fun onReplySent() {
        dismiss()
        listener?.onReply(suggestion)
        requireActivity().toast("Reply sent")
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyView()
    }

    interface ReplyCancelListener {
        fun onReply(suggestion: Suggestion)
    }
}