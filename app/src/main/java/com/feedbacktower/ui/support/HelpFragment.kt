package com.feedbacktower.ui.support


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import com.feedbacktower.databinding.FragmentHelpBinding
import com.feedbacktower.util.Constants


class HelpFragment : Fragment() {
    private lateinit var webView: WebView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentHelpBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentHelpBinding) {
        webView = binding.webView
        webView.loadUrl(Constants.HELP_PAGE_URL)
    }
}
