package com.feedbacktower.ui.transactions


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.TransactionAdapter
import com.feedbacktower.databinding.FragmentTransactionBinding
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast


class WalletTransactionsFragment : Fragment() {
    private lateinit var listView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var adapter: TransactionAdapter
    private var isLoading: Boolean? = false
    private var isListEmpty: Boolean? = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentTransactionBinding.inflate(inflater, container, false)
        initUi(binding)
        return binding.root
    }

    private fun initUi(binding: FragmentTransactionBinding) {
        listView = binding.listView
        swipeRefresh = binding.swipeRefresh
        message = binding.message

        //setup list
        listView.setVertical(requireContext())
        adapter = TransactionAdapter()
        listView.adapter = adapter
        isLoading = binding.isLoading
        isListEmpty = binding.isListEmpty
        swipeRefresh.setOnRefreshListener { fetchTransactions() }
        fetchTransactions()
    }

    private fun fetchTransactions() {
        swipeRefresh.isRefreshing = true
        QRTransactionManager.getInstance().getTransactions() { response, error ->
            swipeRefresh.isRefreshing = false
            if (error == null) {
                adapter.submitList(response?.transactions)
                isListEmpty = response?.transactions?.size == 0
            } else {
                requireContext().toast(error.message?:return@getTransactions)
            }
        }
    }
}
