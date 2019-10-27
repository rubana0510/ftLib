package com.feedbacktower.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.feedbacktower.ui.main.BusinessMainActivity
import com.feedbacktower.data.models.User
import com.feedbacktower.ui.main.CustomerMainActivity
import com.feedbacktower.ui.profile.ProfileSetupScreen
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException
import android.net.Uri
import android.text.TextUtils
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import com.feedbacktower.BuildConfig
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.exception.NoConnectivityException
import com.feedbacktower.network.env.Env
import com.feedbacktower.network.env.Environment
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.utilities.qrscanner.BarcodeEncoder
import com.feedbacktower.ui.splash.SplashScreen
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import org.joda.time.DateTimeZone
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import retrofit2.HttpException
import java.net.SocketTimeoutException

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .setDefaultRequestOptions(RequestOptions().apply { placeholder(R.color.grey100) })
        .load(url)
        .into(this)
}

fun ImageView.loadImage(bitmap: Bitmap) {
    Glide.with(this.context)
        .setDefaultRequestOptions(RequestOptions().apply { placeholder(R.color.grey100) })
        .load(bitmap)
        .into(this)
}

fun ImageView.toProfileRound(userId: String) {
    Glide.with(this.context)
        .load("${Env.S3_BASE_URL}user/$userId.jpg")
        .apply(
            RequestOptions().placeholder(
                ContextCompat.getDrawable(
                    this.context,
                    R.drawable.ic_business_placeholder_profile
                )
            ).circleCrop()
        )
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.toMyProfileRound(userId: String) {
    val lastUpdatedAt = AppPrefs.getInstance(this.context).getValue(AppPrefs.PROFILE_LAST_UPDATED) ?: "default-key"
    Glide.with(this.context)
        .load("${Env.S3_BASE_URL}user/$userId.jpg")
        .apply(RequestOptions().signature(ObjectKey(lastUpdatedAt)))
        .apply(RequestOptions().circleCrop())
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.toUserProfileRound(userId: String) {
    Glide.with(this.context)
        .load("${Env.S3_BASE_URL}user/$userId.jpg")
        .apply(RequestOptions().circleCrop())
        .apply(
            RequestOptions().placeholder(
                ContextCompat.getDrawable(
                    this.context,
                    R.drawable.ic_person_outline_black_24dp
                )
            )
        )
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.loadAdImage(path: String) {
    Glide.with(this.context)
        .setDefaultRequestOptions(RequestOptions().apply { placeholder(R.color.grey100) })
        .load("${Env.S3_BASE_URL}$path")
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
        else -> this.toString()
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
    return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
}

fun String.validWebsite(): Boolean {
    return this.contains('.')
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


internal fun Double.toPrice(): String {
    return "₹" + String.format("%.2f", this)
}

internal fun Int.toPrice(): String {
    return "₹" + String.format("%.2f", this)
}

internal fun String.toDate(): String {
    try {
        val dt = DateTime(this, DateTimeZone.UTC)
        val toFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.ENGLISH)
        return toFormat.format(Date(dt.millis))
    } catch (e: ParseException) {
        return "";
    }
}

internal fun Activity.showAppInStore() {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
        )
    )
}

@Suppress("DEPRECATION")
fun <T> Context.isServiceRunning(service: Class<T>): Boolean {
    return (getSystemService(ACTIVITY_SERVICE) as ActivityManager)
        .getRunningServices(Integer.MAX_VALUE)
        .any { it -> it.service.className == service.name }
}

internal fun Activity.logOut() {
    AppPrefs.getInstance(this).authToken = null
    AppPrefs.getInstance(this).user = null
    this.launchActivity<SplashScreen> {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
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

internal fun Long.toRelativeTime(): String {
    try {
        val toFormat = SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.ENGLISH)
        val currentTime: Long = System.currentTimeMillis() / 1000
        val oldTime: Long = this / 1000
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
        return this.toString()
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
    if (AppPrefs.getInstance(context).user?.userType == "CUSTOMER") return false
    val currentBusinessId =
        AppPrefs.getInstance(context).user?.business?.id ?: throw IllegalArgumentException("Invalid user")
    return currentBusinessId == businessId
}

fun LatLng.toArray(): List<Double> {
    return arrayListOf(this.longitude, this.latitude)
}

fun LatLng.toLocation(): com.feedbacktower.data.models.Location {
    return com.feedbacktower.data.models.Location(this.toArray(), "")
}

fun Location?.toLatLng(): LatLng? {
    if (this == null) return null

    return LatLng(latitude, longitude)
}


fun String?.toQrBitmap(): Bitmap? {
    val multiFormatWriter = MultiFormatWriter()
    try {
        val bitMatrix = multiFormatWriter.encode(this, BarcodeFormat.QR_CODE, 300, 300)
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.createBitmap(bitMatrix)
    } catch (e: WriterException) {
        e.printStackTrace()
        return null
    }
}

fun GoogleMap?.zoomToLocation(currLocation: LatLng, zoomLevel: Float = 13f) {
    this?.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, zoomLevel))
}

internal fun File.getMimeType(): String? {
    val uri = Uri.fromFile(this)
    val ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)
}

fun <T> Exception.toErrorResponse(): ApiResponse<T> {
    return when (this) {

        is NoConnectivityException ->
            ApiResponse(
                ApiResponse.ErrorModel("", "You are not connected to the internet", ApiResponse.ErrorType.NO_INTERNET),
                null,
                null
            )

        is HttpException ->
            ApiResponse(
                ApiResponse.ErrorModel(
                    "",
                    "Some error occurred (code${this.code()}: ${this.message()})",
                    ApiResponse.ErrorType.HTTP_EXCEPTION
                ),
                null,
                null
            )

        is SocketTimeoutException ->
            ApiResponse(
                ApiResponse.ErrorModel("", "Could not reach servers", ApiResponse.ErrorType.TIMEOUT),
                null,
                null
            )

        else ->
            ApiResponse(
                ApiResponse.ErrorModel("", "Unknown error occurred", ApiResponse.ErrorType.UNKNOWN),
                null,
                null
            )
    }
}

fun Activity?.hideKeyBoard() {
    if (this != null && !this.isFinishing) {
        val view = this.currentFocus
        if (view != null) {
            val var2 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            var2.hideSoftInputFromWindow(view.windowToken, 2)
        }
    }
}
