package com.feedbacktower.utilities.videotrimmer.interfaces;

import android.net.Uri;

/**
 * Created by Deep Patel
 * (Sr. Android Developer)
 * on 6/4/2018
 */
public interface OnTrimVideoListener {

    void getResult(final Uri uri);

    void cancelAction();
}
