package com.example.user.myapplication.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

class SquareImage :AppCompatImageView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(width, width)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, w, oldw, oldh)
    }
}