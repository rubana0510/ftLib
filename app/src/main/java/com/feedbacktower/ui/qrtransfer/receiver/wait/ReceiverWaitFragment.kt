package com.feedbacktower.ui.qrtransfer.receiver.wait

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.databinding.FragmentReciverWaitBinding
import com.feedbacktower.network.models.ApiResponse
import com.feedbacktower.network.models.QrPaymentStatus
import com.feedbacktower.network.models.QrStatusRecieverResponse
import com.feedbacktower.network.models.QrTxStatus
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast
import javax.inject.Inject


class ReceiverWaitFragment : BaseViewFragmentImpl(), ReceiverWaitContract.View {
    private val TAG = "ReceiverWaitFrag"
    @Inject
    lateinit var presenter: ReceiverWaitPresenter
    private val args: ReceiverWaitFragmentArgs by navArgs()
    private lateinit var binding: FragmentReciverWaitBinding
    private var senderWalletAmount = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as App).appComponent.paymentComponent().create().inject(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReciverWaitBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        presenter.listenForChanges(args.txid)
        binding.onSendRequestClick = View.OnClickListener { sendRequest() }
        return binding.root
    }


    override fun onListenResponse(response: QrStatusRecieverResponse?) {
        response?.let {
            binding.business = it.sender
            binding.transaction = it.txn
            senderWalletAmount = it.txn.amountAvailable
            //requireContext().toast("Status: ${it.txn.status}")

            binding.scanned = it.txn.txStatus == QrTxStatus.SCANNED
            binding.requested = it.txn.txStatus == QrTxStatus.REQUESTED
            binding.approved = it.txn.txStatus == QrTxStatus.APPROVED
            binding.isLoading = it.txn.txStatus == QrTxStatus.REQUESTED

            if (it.txn.txStatus == QrTxStatus.APPROVED) {
                requireContext().toast("Transfer success")
                Handler().postDelayed({
                    activity?.finish()
                }, 1000)
                return
            } else {
                Handler().postDelayed({
                    presenter.listenForChanges(args.txid)
                }, Constants.QR_STATUS_CHECK_INTERVAL)
            }
        }
    }

    private fun sendRequest() {
        val amountText = binding.amountEntered.text?.toString()?.trim()
        if (amountText.isNullOrEmpty()) {
            requireContext().toast("Enter amount")
            return
        }
        val amount = try {
            amountText.toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
        if (amount <= 0 || amount > senderWalletAmount) {
            requireContext().toast("Invalid amount")
            return
        }
        presenter.sendPaymentRequest(args.txid, amount)
    }

    override fun onPaymentRequestResponse(response: QrPaymentStatus?) {
        response?.let {
            requireContext().toast(getString(R.string.request_sent))
            binding.transaction = it.txn
        }
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireActivity().toast(error.message)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroyView()
    }

    override fun onListenShowProgress() {
    }

    override fun onListenHideProgress() {
    }

    override fun onPaymentRequestShowProgress() {
    }

    override fun onPaymentRequestHideProgress() {
    }
}
