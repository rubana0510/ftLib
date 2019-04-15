package com.feedbacktower.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.feedbacktower.R


fun ImageView.loadImage(context: Context, url: String) {
    Glide.with(context)
        .setDefaultRequestOptions(RequestOptions().apply { placeholder(R.color.grey100) })
        .load(url)
        .into(this)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun Float.toRemarkText(): String{
   return when(this){
        0f-> ""
       1f-> "GOOD1"
       2f-> "GOOD2"
       3f-> "GOOD3"
       4f-> "GOOD4"
       5f-> "GOOD5"
       else -> ""
    }
}

fun Int.dpToPx(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return Math.round(this * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

fun isPasswordValid(password: String): Boolean {
    return password.isNotEmpty()
}

fun isPasswordLengthValid(password: String): Boolean {
    return password.length > Constants.MIN_PASSWORD_LENGTH && password.length < Constants.MAX_PASSWORD_LENGTH
}

fun isPhoneValid(phone: String): Boolean {
    return phone.length == 10
}

fun isNameValid(name: String): Boolean {
    return name.isNotEmpty()
}

fun isEmailValid(email: String): Boolean {
    return email.isNotEmpty()
}

inline fun <reified T : Any> Activity.launchActivity(bundle: Bundle? = null, noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent)
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)
