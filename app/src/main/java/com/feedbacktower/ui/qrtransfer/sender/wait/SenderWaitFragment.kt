package com.feedbacktower.ui.qrtransfer.sender.wait

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.feedbacktower.App
import com.feedbacktower.R
import com.feedbacktower.databinding.FragmentSenderWaitBinding
import com.feedbacktower.network.models.*
import com.feedbacktower.ui.base.BaseViewFragmentImpl
import com.feedbacktower.util.Constants
import org.jetbrains.anko.toast
import javax.inject.Inject


class SenderWaitFragment : BaseViewFragmentImpl(), SenderWaitContract.View {
    private val TAG = "SenderWaitFrag"
    @Inject
    lateinit var presenter: SenderWaitPresenter
    private lateinit var binding: FragmentSenderWaitBinding
    private val args: SenderWaitFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().applicationContext as App).appComponent.paymentComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSenderWaitBinding.inflate(inflater, container, false)
        presenter.attachView(this)
        binding.onRequestConfirmClick = View.OnClickListener { presenter.acceptRequest(args.txid) }
        binding.onCancelClick = View.OnClickListener { presenter.cancelTransaction(args.txid) }
        binding.onGoBackClick = View.OnClickListener { requireActivity().finish() }
        presenter.listenForChanges(args.txid)
        return binding.root
    }

    override fun onQrPaymentStatusResponse(response: QrStatusSenderResponse?) {
        response?.let {
            binding.business = it.receiver
            binding.transaction = it.txn
            binding.scanned = it.txn.txStatus == QrTxStatus.SCANNED
            binding.requested = it.txn.txStatus == QrTxStatus.REQUESTED
            binding.approved = it.txn.txStatus == QrTxStatus.APPROVED
            binding.isLoading = it.txn.txStatus == QrTxStatus.SCANNED
            if (it.txn.txStatus == QrTxStatus.APPROVED) {
                requireContext().toast(getString(R.string.transfer_success))
                Handler().postDelayed({
                    requireActivity().finish()
                }, 1000)
                return@let
            }
            Handler().postDelayed({
                presenter.listenForChanges(args.txid)
            }, Constants.QR_STATUS_CHECK_INTERVAL)
        }
    }

    override fun onRequestAccepted(response: QrPaymentStatus?) {
        response?.let {
            binding.transaction = it.txn
        }
    }

    override fun onTransactionCancelled(response: EmptyResponse?) {
        requireActivity().toast(getString(R.string.transfer_cancelled))
        requireActivity().finish()
    }

    override fun onDestroy() {
        presenter.destroyView()
        super.onDestroy()
    }

    override fun showNetworkError(error: ApiResponse.ErrorModel) {
        super.showNetworkError(error)
        requireActivity().toast(error.message)
    }
}
