package com.feedbacktower.fragments.business


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs

import com.feedbacktower.R
import com.feedbacktower.databinding.FragmentReciverWaitBinding
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.network.models.QrTxStatus
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast


class ReceiverWaitFragment : Fragment() {

    private val TAG = "ReceiverWaitFrag"
    private val args: ReceiverWaitFragmentArgs by navArgs()
    private lateinit var binding: FragmentReciverWaitBinding
    //TODO: remove later
    private var foregraound: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReciverWaitBinding.inflate(inflater, container, false)
        listenForChange(args.txid)
        binding.onSendRequestClick = View.OnClickListener { sendRequest() }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        foregraound = true
    }

    override fun onPause() {
        super.onPause()
        foregraound = false
    }

    private fun listenForChange(data: String) {
        //  qrImage.gone()
        Log.d(TAG, "Checking Status...")
        QRTransactionManager.getInstance()
            .checkStatusReceiver(data) { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@checkStatusReceiver
                }
                response?.let {
                    binding.business = it.sender
                    binding.transaction = it.txn
                    //requireContext().toast("Status: ${it.txn.status}")

                    binding.scanned = it.txn.txStatus == QrTxStatus.SCANNED
                    binding.requested = it.txn.txStatus == QrTxStatus.REQUESTED
                    binding.approved = it.txn.txStatus == QrTxStatus.APPROVED
                    binding.isLoading = it.txn.txStatus == QrTxStatus.SCANNED || it.txn.txStatus == QrTxStatus.REQUESTED

                    if (it.txn.txStatus == QrTxStatus.APPROVED) {
                        Handler().postDelayed({
                            activity?.finish()
                        }, 1000)
                    } else {

                        Handler().postDelayed({
                            listenForChange(data)
                        }, Constants.QR_STATUS_CHECK_INTERVAL)
                    }

                }
            }
    }

    private fun sendRequest() {
        val code = args.txid
        val amount = binding.amountEntered.text.toString().trim().toDouble()
        if (amount > binding.business!!.walletAmount) {
            requireContext().toast("Invalid amount")
            return
        }
        QRTransactionManager.getInstance()
            .requestPayment(code, amount) { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@requestPayment
                }
                response?.let {
                    requireContext().toast("Request sent")
                    binding.transaction = it.txn
                }
            }
    }
}
