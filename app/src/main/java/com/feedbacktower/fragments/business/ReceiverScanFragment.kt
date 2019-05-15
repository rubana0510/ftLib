package com.feedbacktower.fragments.business


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.feedbacktower.R
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.qrscanner.*
import com.feedbacktower.util.PermissionManager
import kotlinx.android.synthetic.main.fragment_reciver_scan.view.*
import org.jetbrains.anko.toast


class ReceiverScanFragment : Fragment() {
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
                Toast.makeText(requireActivity(), "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                scanned(it.text)
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            // or ErrorCallback.SUPPRESS
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireActivity(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        view.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
        return view
    }

    private fun scanned(data: String) {
        QRTransactionManager.getInstance()
            .scan(data) { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@scan
                }
                if (response == null) throw  IllegalStateException("Scan QR response cannot be null")
                requireContext().toast("Qr scan success: ${response.txn.id}, please scan")
            }
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
