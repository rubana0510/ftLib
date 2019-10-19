package com.feedbacktower.ui.qrtransfer.receiver.scan


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.ScanQrResponse
import com.feedbacktower.utilities.qrscanner.*
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.ui.qrtransfer.sender.SenderActivity
import com.feedbacktower.util.PermissionManager
import com.feedbacktower.util.launchActivity
import kotlinx.android.synthetic.main.fragment_reciver_scan.view.*
import org.jetbrains.anko.toast


class ReceiverScanFragment : BaseViewFragmentImpl(), ReceiverScanContract.View {

    private lateinit var presenter: ReceiverScanPresenter
    private lateinit var codeScanner: CodeScanner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reciver_scan, container, false)
        PermissionManager.getInstance().requestCameraPermission(requireActivity())
        codeScanner = CodeScanner(requireContext(), view.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                presenter.verifyScannedData(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            // or ErrorCallback.SUPPRESS
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireActivity(), "Camera initialization failed",
                    Toast.LENGTH_LONG
                ).show()
                requireActivity().finish()
            }
        }
        view.showQr.setOnClickListener { requireActivity().launchActivity<SenderActivity>() }
        view.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
        presenter = ReceiverScanPresenter()
        presenter.attachView(this)
        return view
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun showProgress() {
        super.showProgress()

    }

    override fun dismissProgress() {
        super.dismissProgress()

    }

    override fun onVerified(response: ScanQrResponse?) {
        if (response != null) {
            ReceiverScanFragmentDirections.actionReceiverScanFragmentToReceiverWaitFragment(
                response.txn.id
            ).let {
                findNavController().navigate(it)
            }
        } else {
            requireActivity().toast(getString(R.string.some_error_try_again))
            codeScanner.startPreview()
        }
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireContext().toast(error.message)
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
