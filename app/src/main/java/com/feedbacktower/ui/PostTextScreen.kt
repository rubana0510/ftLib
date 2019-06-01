package com.feedbacktower.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.feedbacktower.R
import com.feedbacktower.network.manager.PostManager
import com.feedbacktower.util.disable
import com.feedbacktower.util.enable
import kotlinx.android.synthetic.main.activity_post_text_screen.*
import org.jetbrains.anko.toast

class PostTextScreen : AppCompatActivity() {

    private var postId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_text_screen)

        val postText = intent?.getStringExtra("TEXT")
        postId = intent?.getStringExtra("POST_ID")
        editText.setText(postText)
        postButton.setOnClickListener {
            val text: String? = editText.text.toString()
            if (text.isNullOrEmpty()) return@setOnClickListener
            postButton.disable()

            if (postId == null) {
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
            } else {
                PostManager.getInstance()
                    .editTextPost(postId!!, text) { response, error ->
                        postButton.enable()
                        if (error != null) {
                            toast(error.message ?: getString(R.string.default_err_message))
                            return@editTextPost
                        }
                        toast("Post edited")
                        finish()
                    }
            }

        }
    }
}
