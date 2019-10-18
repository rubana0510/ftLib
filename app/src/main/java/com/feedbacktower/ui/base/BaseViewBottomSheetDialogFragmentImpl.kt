package com.feedbacktower.ui.base

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.feedbacktower.callbacks.BottomSheetOnStateChanged
import com.feedbacktower.network.models.ApiResponse
import com.google.android.material.bottomsheet.BottomSheetBehavior

open class BaseViewBottomSheetDialogFragmentImpl : BaseBottomSheetDialogFragment(), BaseView {
    val onStateChangedCallback = BottomSheetOnStateChanged { _, newState ->
        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            dismiss()
        }
    }

    fun setUpBehaviour(contentView: View) {
        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        if (behavior != null && behavior is BottomSheetBehavior) {
            behavior.setBottomSheetCallback(onStateChangedCallback)
        }
        (contentView.parent as View).setBackgroundColor(ContextCompat.getColor(context!!, android.R.color.transparent))
    }

    override fun showProgress() {

    }

    override fun dismissProgress() {
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
    }
}