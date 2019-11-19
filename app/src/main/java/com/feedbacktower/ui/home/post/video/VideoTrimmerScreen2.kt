package com.feedbacktower.ui.home.post.video

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.util.Constants
import com.feedbacktower.util.logd
import com.feedbacktower.utilities.videotrimmer_kt.interfaces.VideoTrimmingListener
import kotlinx.android.synthetic.main.activity_video_trimmer_screen2.*
import kotlinx.android.synthetic.main.dialog_loading.view.*
import org.jetbrains.anko.toast
import java.io.File
import javax.inject.Inject

class VideoTrimmerScreen2 : BaseViewActivityImpl(), VideoPostContract.View, VideoTrimmingListener {
    @Inject
    lateinit var presenter: VideoPostPresenter

    private lateinit var progressDialog: AlertDialog
    private lateinit var progressTitle: TextView
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_trimmer_screen2)
        (applicationContext as App).appComponent.uploadPostComponent().create().inject(this)
        presenter.attachView(this)
        initProgressBar()
        val inputVideoUri: Uri? = intent?.getParcelableExtra("EXTRA_INPUT_URI")
        if (inputVideoUri == null) {
            finish()
            return
        }

        videoTrimmerView.setMaxDurationInMs(Constants.Media.MAX_VIDEO_LENGTH)
        videoTrimmerView.setOnK4LVideoListener(this)
        val parentFolder = getExternalFilesDir(null)!!
        parentFolder.mkdirs()
        val fileName = "post_${System.currentTimeMillis()}.mp4"
        val trimmedVideoFile = File(parentFolder, fileName)
        videoTrimmerView.setDestinationFile(trimmedVideoFile)
        videoTrimmerView.setVideoURI(inputVideoUri)
        videoTrimmerView.setVideoInformationVisibility(true)
        sendButton.setOnClickListener {
            caption.clearFocus()
            Handler().post {
                videoTrimmerView.initiateTrimming()
            }
        }
    }

    private fun initProgressBar() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        progressDialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
        progressTitle = view.title
        progressBar = view.progress
    }

    override fun onTrimStarted() {
        trimmingProgressView.visibility = View.VISIBLE
        progressTitle.text = getString(R.string.preparing_video_for_upload)
        progressBar.isIndeterminate = true
        progressDialog.show()
    }

    override fun onFinishedTrimming(uri: Uri?) {
        logd("onFinishedTrimming: ${uri?.path}")
        progressBar.isIndeterminate = false
    }

    override fun onErrorWhileViewingVideo(what: Int, extra: Int) {
        trimmingProgressView.visibility = View.GONE
        finish()
    }

    override fun onVideoPrepared() {
        //        Toast.makeText(TrimmerActivity.this, "onVideoPrepared", Toast.LENGTH_SHORT).show();
    }

    override fun onCompressStarted() {
    }

    override fun onFinishedCompressing(uri: Uri?) {
        if (uri == null) {
            toast(getString(R.string.some_error_try_again))
            finish()
            return
        }
        presenter.postVideo(File(uri.path), caption.text.toString().trim())
    }

    override fun onCompressFailed() {
    }

    override fun onCompressProgress(progress: Float?) {
        val p: Int = (progress ?: 0f).toInt()
        progressBar.progress = p
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }

    override fun onVideoPosted() {
        toast(getString(R.string.video_uploaded))
        finish()
    }

    override fun showProgress() {
        super.showProgress()
        progressTitle.text = getString(R.string.uploading_video)
        progressBar.isIndeterminate = true
    }

    override fun dismissProgress() {
        super.dismissProgress()
        progressDialog.dismiss()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        toast(error.message)
        finish()
    }

    override fun onStop() {
        super.onStop()
        if (progressDialog.isShowing)
            progressDialog.dismiss()
    }

}
