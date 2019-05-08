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
import com.feedbacktower.network.manager.SuggestionsManager
import kotlinx.android.synthetic.main.dialog_send_suggestion.view.*
import org.jetbrains.anko.toast


class SendSuggestionDialog : BottomSheetDialogFragment() {

    private val onStateChangedCallback = BottomSheetOnStateChanged { _, newState ->
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismiss()
        }
    }

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
        sendSuggestionButton = contentView.sendSuggestionButton
        suggestionMessageInput = contentView.suggestionMessageInput

        closeButton.setOnClickListener { dialog.dismiss() }
        sendSuggestionButton.setOnClickListener { sendSuggestion() }
    }

    private fun sendSuggestion() {
        val suggestionMessage: String = suggestionMessageInput.text.toString()
        if (suggestionMessage.isNotEmpty()) return
        SuggestionsManager.getInstance()
            .addSuggestion(
                hashMapOf<String, Any?>(
                    "businessId" to businessId,
                    "message" to suggestionMessage
                )
            ){_,error->
                if(error == null){
                    dialog.dismiss()
                    ctx.toast("Sending suggestion..\"$suggestionMessage\"")
                }

            }




    }

}