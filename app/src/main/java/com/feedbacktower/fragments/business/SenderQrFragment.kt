package com.feedbacktower.fragments.business


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.network.models.QrTxStatus
import com.feedbacktower.util.Constants
import com.feedbacktower.util.gone
import com.feedbacktower.util.toQrBitmap
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.fragment_sender_qr.view.*
import org.jetbrains.anko.toast


class SenderQrFragment : Fragment() {
    private val TAG = "SenderQrFragment"
    private lateinit var qrImage: ImageView
    private lateinit var progress: ProgressBar
    private var currentTransactionData: String? = null
    //TODO: remove later
    private var foregraound: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sender_qr, container, false)
        qrImage = view.qrImage
        progress = view.progress
        return view
    }

    private fun fetchQrData() {
        progress.visible()
        qrImage.gone()
        QRTransactionManager.getInstance()
            .generate { response, error ->
                progress.gone()
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@generate
                }
                if (response == null) throw  IllegalStateException("Generate QR response cannot be null")
                //requireContext().toast("Qr code generated: ${response.txn.id}, please scan")
                currentTransactionData = response.txn.id
                showImage()
            }
    }

    private fun showImage() {
        currentTransactionData?.let {
            val bitmap = it.toQrBitmap()
            qrImage.setImageBitmap(bitmap)
            qrImage.visible()
            listenForScanned()
        }
    }

    override fun onResume() {
        super.onResume()
        foregraound = true
        if (currentTransactionData == null)
            fetchQrData()
        else
            listenForScanned()
    }

    override fun onPause() {
        super.onPause()
        foregraound = false
    }

    private fun listenForScanned() {
        //  qrImage.gone()
        val data = currentTransactionData ?: return
        Log.d(TAG, "Checking Status...")
        QRTransactionManager.getInstance()
            .checkStatusSender(data) { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@checkStatusSender
                }
                response?.let {
                    Log.d(TAG, "QrTxStatus: ${it.txn.status}")
                    if (it.txn.txStatus == QrTxStatus.GENERATED) {
                        Handler().postDelayed({
                            if (foregraound)
                                listenForScanned()
                        }, Constants.QR_STATUS_CHECK_INTERVAL)
                        return@let
                    } else if (it.txn.txStatus == QrTxStatus.SCANNED) {
                        //scanned
                        Log.d(TAG, "SCANNED: ${response.receiver}")
                        SenderQrFragmentDirections.actionSenderQrFragmentToSenderWaitFragment(response.txn.id)
                            .let { dir ->
                                findNavController().navigate(dir)
                            }
                    } else {
                        requireActivity().finish()
                    }

                }
            }
    }
}
