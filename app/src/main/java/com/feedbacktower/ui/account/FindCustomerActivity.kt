package com.feedbacktower.ui.account

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import com.feedbacktower.R
import com.feedbacktower.data.AppPrefs
import com.feedbacktower.network.manager.ProfileManager
import com.feedbacktower.network.models.FindCustomerResponse
import com.feedbacktower.qrscanner.AutoFocusMode
import com.feedbacktower.qrscanner.CodeScanner
import com.feedbacktower.qrscanner.DecodeCallback
import com.feedbacktower.qrscanner.ErrorCallback
import com.feedbacktower.qrscanner.ScanMode
import com.feedbacktower.util.loadImage
import com.feedbacktower.util.toProfileRound
import com.feedbacktower.util.toQrBitmap
import kotlinx.android.synthetic.main.activity_find_customer.*
import kotlinx.android.synthetic.main.dialog_customer_profile.view.*
import kotlinx.android.synthetic.main.dialog_my_qr.view.*
import org.jetbrains.anko.toast
import java.lang.IllegalStateException

class FindCustomerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private val myQr: Bitmap? by lazy { createMyQr() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_customer)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                scanned(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            // or ErrorCallback.SUPPRESS
            runOnUiThread {
                toast("Camera initialization error: ${it.message}")
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
        showQr.setOnClickListener {
            showQrDialog()
        }
    }

    private fun scanned(data: String) {
        ProfileManager.getInstance()
            .find(data) { response, error ->
                if (error != null || response == null) {
                    toast(error?.message ?: getString(R.string.default_err_message))
                    return@find
                }
                showInDialog(response)
            }
    }

    private fun showInDialog(response: FindCustomerResponse) {
        if (isFinishing) return
        val alertDialog = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.dialog_customer_profile, null)
        alertDialog.setView(view)
        view.apply {
            userName.text = getString(R.string.fullName, response.user.firstName, response.user.lastName)
            profileImage.toProfileRound(response.user.id)
            closeButton.setOnClickListener { alertDialog.dismiss() }
        }
        alertDialog.show()
    }

    private fun showQrDialog() {
        if (isFinishing) return
        val alertDialog = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.dialog_my_qr, null)
        alertDialog.setView(view)
        view.apply {
            if (myQr == null) {
                toast("Could not show your QR")
                return
            }
            myQr?.let {
                qrImage.loadImage(it)
            }
        }
        alertDialog.show()
    }

    private fun createMyQr(): Bitmap? {
        val b = AppPrefs.getInstance(this@FindCustomerActivity).user?.id
            ?: throw IllegalStateException("User cannot be null")
        return b.toQrBitmap()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}
