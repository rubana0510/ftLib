package com.feedbacktower.ui.support


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.feedbacktower.databinding.FragmentHelpBinding
import com.feedbacktower.network.env.Env
import com.feedbacktower.network.env.Environment
import com.feedbacktower.util.Constants


class HelpFragment : Fragment() {
    private lateinit var binding: FragmentHelpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHelpBinding.inflate(inflater, container, false)
        initUi()
        return binding.root
    }

    private fun initUi() {
        binding.webView.settings.apply { javaScriptEnabled = true }
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl("${Env.S3_BASE_URL}/help/")
    }
}
