package com.feedbacktower.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.feedbacktower.R
import com.feedbacktower.callbacks.BottomSheetOnStateChanged
import com.feedbacktower.data.models.Suggestion
import com.feedbacktower.databinding.DialogReplySuggestionBinding
import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import kotlinx.android.synthetic.main.dialog_reply_suggestion.view.*
import org.jetbrains.anko.toast


class ReplySuggestionDialog : BottomSheetDialogFragment() {

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

    private val onStateChangedCallback = BottomSheetOnStateChanged { _, newState ->
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismiss()
        }
    }

    private lateinit var ctx: Context
    private lateinit var replyMessageInput: EditText
    private lateinit var sendReplyButton: Button
    private lateinit var suggestion: Suggestion
    var listener: ReplyCancelListener? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        ctx = activity ?: return

        suggestion = arguments?.getSerializable("SUGGESTION") as? Suggestion
            ?: throw IllegalArgumentException("Wrong suggestion args")

        val contentView = View.inflate(context, R.layout.dialog_reply_suggestion, null)
        val binding = DialogReplySuggestionBinding.bind(contentView)
        binding.suggestion = suggestion
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
        sendReplyButton = contentView.sendReplyButton
        replyMessageInput = contentView.replyMessageInput
        sendReplyButton.setOnClickListener { sendSuggestionReply() }
    }

    private fun showLoading(){
        sendReplyButton.text = "PLEASE WAIT..."
        sendReplyButton.disable()
        isCancelable = false
    }
    private fun hideLoading(){
        sendReplyButton.text = "REPLY"
        sendReplyButton.enable()
        isCancelable = true
    }

    private fun sendSuggestionReply() {
        val replyMessage: String = replyMessageInput.text.toString()
        if (replyMessage.isEmpty()) return
        showLoading()
        SuggestionsManager.getInstance()
            .replySuggestion(
                suggestion.id,
                replyMessage
            ) { _, error ->
                hideLoading()
                if (error == null) {
                    dialog.dismiss()
                    listener?.onReply(suggestion)
                    ctx.toast("Sent reply")
                } else {
                    requireContext().toast("Some error occurred")
                }
            }
    }

    interface ReplyCancelListener{
        fun onReply(suggestion: Suggestion)
    }
}