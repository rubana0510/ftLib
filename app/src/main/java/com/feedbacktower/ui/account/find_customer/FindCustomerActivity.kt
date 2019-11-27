package com.feedbacktower.ui.account.find_customer

import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.network.models.FindCustomerResponse
import com.feedbacktower.ui.base.BaseViewActivityImpl
import com.feedbacktower.util.loadImage
import com.feedbacktower.util.toProfileRound
import com.feedbacktower.utilities.qrscanner.*
import kotlinx.android.synthetic.main.activity_find_customer.*
import kotlinx.android.synthetic.main.dialog_customer_profile.view.*
import kotlinx.android.synthetic.main.dialog_my_qr.view.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class FindCustomerActivity : BaseViewActivityImpl(), FindCustomerContract.View {
    @Inject
    lateinit var presenter: FindCustomerPresenter
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_customer)
        (applicationContext as App).appComponent.accountComponent().create().inject(this)
        presenter.attachView(this)
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
                presenter.findCustomer(it.text)
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

    private fun showInDialog(response: FindCustomerResponse) {
        if (isFinishing) return
        val alertDialog = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.dialog_customer_profile, null)
        alertDialog.setView(view)
        view.apply {
            userName.text = getString(R.string.fullName, response.user.firstName, response.user.lastName)
            city.text = response.user.city?.name
            profileImage.toProfileRound(response.user.firstName)
            close1.setOnClickListener { alertDialog.dismiss() }
        }
        alertDialog.show()
    }

    private fun showQrDialog() {
        if (isFinishing) return
        val alertDialog = AlertDialog.Builder(this).create()
        val view = layoutInflater.inflate(R.layout.dialog_my_qr, null)
        alertDialog.setView(view)
        view.apply {
            presenter.getMyQrBitmap()?.let {
                qrImage.loadImage(it)
            }
            close2.setOnClickListener { alertDialog.dismiss() }
        }
        alertDialog.show()
    }


    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onFoundResponse(response: FindCustomerResponse) {
        showInDialog(response)
    }


    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
