package com.feedbacktower.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.feedbacktower.network.models.QrTxStatus
import com.feedbacktower.util.Constants
import com.feedbacktower.util.gone
import com.feedbacktower.util.toRelativeTime
import com.feedbacktower.util.visible

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("loadImage")
fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}

@BindingAdapter("postImage")
fun bindPostImage(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load("${Constants.Service.Secrets.BASE_URL}/posts/$imageUrl")
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

@BindingAdapter("textSafe")
fun bindTextSafe(view: TextView, text: Any) {
    view.text = text.toString()
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
        Glide.with(view.context)
            .load("${Constants.Service.Secrets.BASE_URL}/user/$userId.jpg")
            .apply(RequestOptions().circleCrop())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}


@BindingAdapter("checkedIfOne")
fun bindCheckedIfOne(view: SwipeRefreshLayout, value: Boolean) {
    view.isRefreshing = value
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
    view.visibility = if (value ==  QrTxStatus.REQUESTED) {
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