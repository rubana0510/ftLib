package com.feedbacktower.di.upload_post

import com.feedbacktower.ui.home.post.image.ImagePostActivity
import com.feedbacktower.ui.home.post.text.TextPostActivity
import com.feedbacktower.ui.home.post.video.VideoTrimmerScreen2
import dagger.Subcomponent

@UploadPostScope
@Subcomponent
interface UploadPostComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): UploadPostComponent
    }

    fun inject(activity: ImagePostActivity)
    fun inject(activity: TextPostActivity)
    fun inject(activity: VideoTrimmerScreen2)
}