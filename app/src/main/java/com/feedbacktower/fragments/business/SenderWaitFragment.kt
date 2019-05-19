package com.feedbacktower.fragments.business


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.feedbacktower.R
import com.feedbacktower.databinding.FragmentSenderWaitBinding
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.network.models.QrTxStatus
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast


class SenderWaitFragment : Fragment() {
    private val TAG = "SenderWaitFrag"
    private val args: SenderWaitFragmentArgs by navArgs()
    private lateinit var binding: FragmentSenderWaitBinding
    //TODO: remove later
    private var foregraound: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSenderWaitBinding.inflate(inflater, container, false)
        listenForScanned(args.txid)
        binding.onRequestConfirmClick = View.OnClickListener { acceptRequest() }
        binding.onGoBackClick = View.OnClickListener { requireActivity().finish() }
        return binding.root
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
                    binding.business = it.receiver
                    binding.transaction = it.txn
                    binding.scanned = it.txn.txStatus == QrTxStatus.SCANNED
                    binding.requested = it.txn.txStatus == QrTxStatus.REQUESTED
                    binding.approved = it.txn.txStatus == QrTxStatus.APPROVED
                    binding.isLoading = it.txn.txStatus == QrTxStatus.SCANNED
                    if(it.txn.txStatus == QrTxStatus.APPROVED){
                        requireContext().toast("Transfer success")
                        Handler().postDelayed({
                            activity?.finish()
                        }, 1000)
                        return@checkStatusSender
                    }
                    if (foregraound) {
                        Handler().postDelayed({
                            listenForScanned(data)
                        }, Constants.QR_STATUS_CHECK_INTERVAL)
                    }

                }
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

    private fun acceptRequest() {

        Log.d(TAG, "Checking Status...")
        QRTransactionManager.getInstance()
            .confirmPayment(args.txid, true) { response, error ->
                if (error != null) {
                    requireContext().toast(error.message ?: getString(R.string.default_err_message))
                    return@confirmPayment
                }
                response?.let {

                    binding.transaction = it.txn
                }
            }
    }
}
