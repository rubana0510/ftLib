package com.feedbacktower.adapters

import android.view.View
import android.webkit.URLUtil
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.feedbacktower.BuildConfig
import com.feedbacktower.R
import com.feedbacktower.data.models.Plan
import com.feedbacktower.network.env.Env
import com.feedbacktower.network.models.QrTxStatus
import com.feedbacktower.util.*
import com.feedbacktower.network.env.Environment


@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("loadImage")
fun bindImageFromUrl(view: ImageView, path: String?) {
    Glide.with(view.context)
        .setDefaultRequestOptions(RequestOptions().apply { placeholder(R.color.grey100) })
        .load("${Env.S3_BASE_URL}$path")
        .into(view)
}

@BindingAdapter("postImage")
fun bindPostImage(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load("${Env.S3_BASE_URL}$imageUrl")
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("loadRoundImage")
fun bindImageFromDrawable(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .apply(RequestOptions().circleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("loadDrawableImage")
fun bindImageFromUrlRound(view: ImageView, drawable: Int) {
    Glide.with(view.context)
        .load(drawable)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)
}

@BindingAdapter("isChecked")
fun bindIsChecked(view: ImageButton, count: Int) {
    view.isSelected = count == 1
}

@BindingAdapter("goneIfNull")
fun bindGoneIfNull(view: View, gone: String?) {
    view.visibility = if (gone == null) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("goneIfInvalidLink")
fun bindGoneIfInvalidLink(view: View, link: String?) {
    view.visibility = if (link == null || !URLUtil.isValidUrl(link)) {
        View.GONE
    } else {
        View.VISIBLE
    }
}


@BindingAdapter("goneIfZero")
fun bindGoneIfZero(view: View, value: Any?) {
    view.visibility = if (value == null || value == 0) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("showLoading")
fun bindShowLoading(button: Button, loading: Boolean) {
    button.isEnabled = !loading
    button.text = if (loading) "Please wait..." else "Continue"
}

@BindingAdapter("showRefreshing")
fun bindShowRefreshing(view: SwipeRefreshLayout, loading: Boolean) {
    view.isRefreshing = loading
}


@BindingAdapter("textSafe")
fun bindTextSafe(view: TextView, text: Any) {
    view.text = text.toString()
}

@BindingAdapter("textDouble")
fun bindTextDouble(view: TextView, text: Any) {
    view.text = text.toString()
}

@BindingAdapter("textPrice")
fun bindTextPrice(view: TextView, text: Double) {
    view.text = text.toPrice()
}

@BindingAdapter("planBenefits")
fun bindPlanBenefits(view: TextView, plan: Plan?) {
    plan?.let {
        view.text = "• Get ₹${plan.maxWalletCashback} in wallet" +
                "\n• ${plan.maxTextPost.unlimitedCheck} Text posts" +
                "\n• ${plan.maxPhotoPost.unlimitedCheck} Photo posts" +
                "\n• ${plan.maxVideoPost.unlimitedCheck} Video posts"

    }
}

private val Int.unlimitedCheck: String
    get() {
        return if (this == -1) return "Unlimited" else this.toString()
    }

@BindingAdapter("toDate")
fun bindToDate(view: TextView, text: String?) {
    text?.let {
        view.text = text.toRelativeTime()
    }
}

@BindingAdapter("toProfileRound")
fun bindToProfileRound(view: ImageView, userId: String?) {
    if (!userId.isNullOrEmpty()) {
        view.toProfileRound(userId)
    }
}

@BindingAdapter("toMyProfileRound")
fun bindToMyProfileRound(view: ImageView, userId: String?) {
    if (!userId.isNullOrEmpty()) {
        view.toMyProfileRound(userId)
    }
}

@BindingAdapter("checkedIfOne")
fun bindCheckedIfOne(view: ImageButton, value: Int) {
    view.isSelected = value == 1
}


@BindingAdapter("showIfApproved")
fun bindShowIfApproved(view: View, value: QrTxStatus?) {
    view.visibility = if (value == QrTxStatus.APPROVED) {
        View.GONE
    } else {
        View.VISIBLE
    }
}


@BindingAdapter("showIfRequested")
fun bindShowIfRequested(view: View, value: QrTxStatus?) {
    view.visibility = if (value == QrTxStatus.REQUESTED) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("showIfScanned")
fun bindShowIfScanned(view: View, value: QrTxStatus?) {
    view.visibility = if (value == QrTxStatus.SCANNED) {
        View.GONE
    } else {
        View.VISIBLE
    }


}