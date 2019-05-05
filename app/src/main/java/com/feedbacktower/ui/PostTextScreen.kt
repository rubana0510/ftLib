package com.feedbacktower.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.feedbacktower.R
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import kotlinx.android.synthetic.main.activity_post_text_screen.*
import org.jetbrains.anko.toast

class PostTextScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_text_screen)
        postButton.setOnClickListener {
            val text: String? = editText.text.toString()
            if (text.isNullOrEmpty()) return@setOnClickListener
            postButton.disable()
            PostManager.getInstance()
                .createTextPost(text) { response, error ->
                    postButton.enable()
                    if (error != null) {
                        toast(error.message ?: getString(R.string.default_err_message))
                        return@createTextPost
                    }
                    toast("Post shared")
                    finish()
                }
        }
    }
}
