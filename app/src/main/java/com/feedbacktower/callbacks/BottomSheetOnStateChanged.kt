package com.feedbacktower.callbacks

import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior

class BottomSheetOnStateChanged(val sheetOnStateChanged: (bottomSheet: View, newState: Int) -> Unit) :
    BottomSheetBehavior.BottomSheetCallback() {
    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

    override fun onStateChanged(bottonSheet: View, newState: Int) {
        sheetOnStateChanged(bottonSheet, newState)
    }
}