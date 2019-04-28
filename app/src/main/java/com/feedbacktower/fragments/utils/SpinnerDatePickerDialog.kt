package com.feedbacktower.fragments.utils

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
import com.feedbacktower.util.getMaxDob
import kotlinx.android.synthetic.main.dialog_date_picker.view.*


class SpinnerDatePickerDialog : BottomSheetDialogFragment() {

    companion object {

        fun getInstance(): SpinnerDatePickerDialog {
            return SpinnerDatePickerDialog()
        }
    }

    private val onStateChangedCallback = BottomSheetOnStateChanged { _, newState ->
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismiss()
        }
    }

    private lateinit var ctx: Context
    private lateinit var selectDateButton: Button
    private lateinit var datePicker: DatePicker
    private var listener: OnDateSelectedListener? = null

    fun setDateSelectListener(listener: OnDateSelectedListener) {
        this.listener = listener
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        ctx = activity ?: return
        val contentView = View.inflate(context, R.layout.dialog_date_picker, null)
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
        selectDateButton = contentView.selectDateButton
        datePicker = contentView.datePicker
        datePicker.maxDate = getMaxDob()
        datePicker.updateDate(1990, 0, 1)
        selectDateButton.setOnClickListener {
            listener?.onDateSelect(datePicker.dayOfMonth, datePicker.month, datePicker.year)
            dismiss()
        }
    }


    interface OnDateSelectedListener {
        fun onDateSelect(dayOfMonth: Int, month: Int, year: Int)
    }

}