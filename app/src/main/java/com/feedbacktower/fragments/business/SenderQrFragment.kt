package com.feedbacktower.fragments.business


import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.network.models.QrTxStatus
import com.feedbacktower.qrscanner.BarcodeEncoder
import com.feedbacktower.util.Constants
import com.feedbacktower.util.gone
import com.feedbacktower.util.visible
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.fragment_sender_qr.view.*
import org.jetbrains.anko.toast
import java.lang.IllegalStateException


class SenderQrFragment : Fragment() {
    private val TAG = "SenderQrFragment"
    private lateinit var qrImage: ImageView
    //TODO: remove later
    private var foregraound: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sender_qr, container, false)
        qrImage = view.qrImage
        generateQr()
        return view
    }

    private fun generateQr() {
        qrImage.gone()
        QRTransactionManager.getInstance()
            .generate { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@generate
                }
                if (response == null) throw  IllegalStateException("Generate QR response cannot be null")
                requireContext().toast("Qr code generated: ${response.txn.id}, please scan")
                val bitmap = printQRCode(response.txn.id)
                qrImage.setImageBitmap(bitmap)
                qrImage.visible()
                listenForScanned(response.txn.id)
            }
    }

    override fun onResume() {
        super.onResume()
        foregraound = true
    }

    override fun onPause() {
        super.onPause()
        foregraound = false
    }

    private fun listenForScanned(data: String) {
        //  qrImage.gone()
        Log.d(TAG, "Checking Status...")
        QRTransactionManager.getInstance()
            .checkStatusSender(data) { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@checkStatusSender
                }
                response?.let {
                    Log.d(TAG, "QrTxStatus: ${it.txn.status}")
                    if (it.txn.txStatus != QrTxStatus.SCANNED) {
                        Handler().postDelayed({
                            if (foregraound)
                                listenForScanned(data)
                        }, Constants.QR_STATUS_CHECK_INTERVAL)
                        return@let
                    } else {
                        //scanned
                        Log.d(TAG, "SCANNED: ${response.receiver}")
                        requireContext().toast("Code scanned by: ${response.receiver.name}")
                        SenderQrFragmentDirections.actionSenderQrFragmentToSenderWaitFragment(response.txn.id)
                            .let { dir ->
                                findNavController().navigate(dir)
                            }
                    }

                }
            }
    }


    private fun printQRCode(textToQR: String): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(textToQR, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: WriterException) {
            e.printStackTrace()
            return null
        }

    }
}
