package com.feedbacktower.ui.suggestions.send

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.feedbacktower.R
import com.feedbacktower.fragments.BusinessDetailFragment
import com.feedbacktower.network.manager.SuggestionsManager
import com.feedbacktower.ui.base.BaseViewBottomSheetDialogFragmentImpl
import kotlinx.android.synthetic.main.dialog_send_suggestion.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast


class SendSuggestionDialog(val listener: BusinessDetailFragment.UpdateListener?) :
    BaseViewBottomSheetDialogFragmentImpl() {

    private lateinit var ctx: Context
    private lateinit var closeButton: ImageButton
    private lateinit var suggestionMessageInput: EditText
    private lateinit var sendSuggestionButton: Button
    private lateinit var businessId: String

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        ctx = activity ?: return
        businessId = arguments?.getString("businessId")!!
        val contentView = View.inflate(context, R.layout.dialog_send_suggestion, null)
        dialog.setContentView(contentView)
        setUpBehaviour(contentView)
        initUI(contentView)
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
        isCancelable = false
        SuggestionsManager.getInstance()
            .addSuggestion(
                hashMapOf(
                    "businessId" to businessId,
                    "message" to suggestionMessage
                )
            ) { _, error ->
                isCancelable = true
                if (error == null) {
                    dismiss()
                    //listener?.update()
                    ctx.toast("Suggestion sent")
                } else if (error.message != null && (error.message?.contains("Limit") == true || error.message?.contains(
                        "reached"
                    ) == true)
                ) {
                    requireContext().alert(
                        "You have already sent Suggestion for this business in this month",
                        "Limit reached!",
                        {}
                    ).show()
                } else {
                    ctx.toast(error.message ?: getString(R.string.default_err_message))
                }
            }
    }

}