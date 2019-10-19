package com.feedbacktower.ui.qrtransfer.sender.showqr

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import com.feedbacktower.R
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.QrPaymentStatus
import com.feedbacktower.network.models.QrStatusSenderResponse
import com.feedbacktower.network.models.QrTxStatus
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import com.feedbacktower.util.gone
import com.feedbacktower.util.toQrBitmap
import com.feedbacktower.util.visible
import kotlinx.android.synthetic.main.fragment_sender_qr.view.*
import org.jetbrains.anko.toast


class SenderQrFragment : BaseViewFragmentImpl(), SenderQrContract.View {
    private val TAG = "SenderQrFragment"
    private lateinit var presenter: SenderQrPresenter
    private lateinit var qrImage: ImageView
    private lateinit var progress: ProgressBar
    private var currentTransactionData: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sender_qr, container, false)
        presenter = SenderQrPresenter()
        presenter.attachView(this)
        qrImage = view.qrImage
        progress = view.progress
        return view
    }

    override fun showProgress() {
        super.showProgress()
        progress.visible()
        qrImage.gone()
    }

    override fun dismissProgress() {
        super.dismissProgress()
        progress.gone()
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
        if (currentTransactionData == null)
            presenter.fetchQrData()
        else
            listenForScanned()
    }

    private fun listenForScanned() {
        val data = currentTransactionData ?: return
        presenter.listenForChanges(data)
    }

    override fun onQrPaymentStatusResponse(response: QrStatusSenderResponse?) {
        response?.let {
            when {
                it.txn.txStatus == QrTxStatus.GENERATED -> {
                    Handler().postDelayed({
                        listenForScanned()
                    }, Constants.QR_STATUS_CHECK_INTERVAL)
                    return@let
                }
                it.txn.txStatus == QrTxStatus.SCANNED -> {
                    //scanned
                    SenderQrFragmentDirections.actionSenderQrFragmentToSenderWaitFragment(
                        response.txn.id
                    ).let { dir ->
                        findNavController().navigate(dir)
                    }
                }
                else -> requireActivity().finish()
            }
        }
    }

    override fun onQrDataFetched(response: QrPaymentStatus?) {
        if (response == null) {
            requireActivity().toast(getString(R.string.some_error_try_again))
        } else {
            currentTransactionData = response.txn.id
            showImage()
        }
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireActivity().toast(error.message)
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }
}
