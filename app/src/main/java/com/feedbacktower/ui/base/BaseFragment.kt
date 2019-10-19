package com.feedbacktower.ui.base

import androidx.fragment.app.Fragment
import com.feedbacktower.util.hideKeyBoard

open class BaseFragment: Fragment() {
    override fun onDestroy() {
        requireActivity().hideKeyBoard()
        super.onDestroy()
    }
}