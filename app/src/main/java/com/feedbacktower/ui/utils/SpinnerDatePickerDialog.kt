package com.feedbacktower.ui.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.*
import com.feedbacktower.R
import com.feedbacktower.ui.base.BaseViewBottomSheetDialogFragmentImpl
import com.feedbacktower.util.getMaxDob
import kotlinx.android.synthetic.main.dialog_date_picker.view.*


class SpinnerDatePickerDialog : BaseViewBottomSheetDialogFragmentImpl() {

    companion object {
        fun getInstance(): SpinnerDatePickerDialog {
            return SpinnerDatePickerDialog()
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
        setUpBehaviour(contentView)
        initUI(contentView)
    }

    private fun initUI(contentView: View) {
        selectDateButton = contentView.selectDateButton
        datePicker = contentView.datePicker
        datePicker.maxDate = getMaxDob()
        //datePicker.updateDate(1990, 0, 1)
        selectDateButton.setOnClickListener {
            listener?.onDateSelect(datePicker.dayOfMonth, datePicker.month, datePicker.year)
            dismiss()
        }
    }


    interface OnDateSelectedListener {
        fun onDateSelect(dayOfMonth: Int, month: Int, year: Int)
    }

}