package com.feedbacktower.utilities.videotrimmer.interfaces;

import android.net.Uri;


public interface OnTrimVideoListener {

    void getResult(final String caption, final Uri uri);

    void cancelAction();
}
