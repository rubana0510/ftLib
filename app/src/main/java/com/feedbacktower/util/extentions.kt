package com.feedbacktower.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.feedbacktower.BusinessMainActivity
import com.feedbacktower.data.models.User
import com.feedbacktower.ui.CustomerMainActivity
import com.feedbacktower.ui.ProfileSetupScreen
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException
import android.net.Uri
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

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

fun View.enable() {
    this.isEnabled = true
}

fun View.disable() {
    this.isEnabled = false
}


fun Float.toRemarkText(): String {
    return when (this) {
        0f -> ""
        1f -> "Poor"
        2f -> "Satisfactory"
        3f -> "Good"
        4f -> "Very Good"
        5f -> "Excellent"
        else ->  this.toString()
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

fun String.noValidWebsite(): Boolean {
    return false
}

fun Activity.navigateUser(user: User) {
    if (user.userType == "CUSTOMER") {
        if (!user.profileSetup) {
            launchActivity<ProfileSetupScreen> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            launchActivity<CustomerMainActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }

    } else if (user.userType == "BUSINESS") {
        launchActivity<BusinessMainActivity> {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
}

inline fun <reified T : Any> Activity.launchActivity(bundle: Bundle? = null, noinline init: Intent.() -> Unit = {}) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent)
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)


//recyclerview utils
enum class Orientation { H, V }

internal fun RecyclerView.setLinearLayoutManager(context: Context, orientation: Orientation = Orientation.V) {
    if (orientation == Orientation.V)
        layoutManager = LinearLayoutManager(context)
    else
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
}

internal fun RecyclerView.setItemAnimator() {
    itemAnimator = DefaultItemAnimator()
}

internal fun RecyclerView.enableSeparator(context: Context) {
    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
}

internal fun RecyclerView.setVertical(context: Context) {
    layoutManager = LinearLayoutManager(context)
    itemAnimator = DefaultItemAnimator()
    setHasFixedSize(true)
}

internal fun RecyclerView.setHorizontal(context: Context) {
    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    itemAnimator = DefaultItemAnimator()
    setHasFixedSize(true)
}

internal fun RecyclerView.setGrid(context: Context, span: Int) {
    layoutManager = GridLayoutManager(context, span)
    itemAnimator = DefaultItemAnimator()
    setHasFixedSize(true)
}

internal fun getMaxDob(): Long {
    val cal: Calendar = Calendar.getInstance()
    cal.timeInMillis = System.currentTimeMillis()
    return cal.timeInMillis
}

internal fun Int.noZero(): String {
    return if (this == 0) "-"
    else this.toString()
}

internal fun String.toRelativeTime(): String {
    try {
        val dt = DateTime(this, DateTimeZone.UTC)

        val toFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.ENGLISH)
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val currentTime: Long = System.currentTimeMillis() / 1000
        val oldTime: Long = dt.millis / 1000
        val diff = currentTime - oldTime
        val secs = diff.toInt()
        val mins = diff.toInt() / 60
        val hours = diff.toInt() / (60 * 60)

        return if (secs < 60)
            "Just now"
        else if (mins < 60)
            "$mins min${if (mins == 1) "" else "s"} ago"
        else if (hours < 24)
            "$hours hour${if (hours == 1) "" else "s"} ago"
        else
            toFormat.format(Date(oldTime * 1000))
    } catch (e: ParseException) {
        return this
    }

}

fun String.toRequestBody(): RequestBody = RequestBody.create(MediaType.parse("text/plain"), this)

fun Int.toRequestBody(): RequestBody = RequestBody.create(MediaType.parse("text/plain"), this.toString())

fun Activity.uriToFile(_uri: Uri): File {
    var filePath: String? = null
    if (_uri != null && "content" == _uri.getScheme()) {
        val cursor = this.contentResolver.query(
            _uri,
            arrayOf(android.provider.MediaStore.Images.ImageColumns.DATA),
            null,
            null,
            null
        )
        cursor.moveToFirst()
        filePath = cursor.getString(0)
        cursor.close()
    } else {
        filePath = _uri.getPath()
    }
    return File(filePath)
}

fun isCurrentBusiness(businessId: String, context: Context): Boolean {
    val currentBusinessId =
        AppPrefs.getInstance(context).user?.business?.id ?: throw IllegalArgumentException("Invalid user")
    return currentBusinessId == businessId
}