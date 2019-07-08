package com.feedbacktower.ui.transactions


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.feedbacktower.adapters.TransactionAdapter
import com.feedbacktower.callbacks.ScrollListener
import com.feedbacktower.data.models.QrTransaction
import com.feedbacktower.databinding.FragmentTransactionBinding
import com.feedbacktower.network.manager.QRTransactionManager
import com.feedbacktower.util.Constants
import com.feedbacktower.util.setVertical
import org.jetbrains.anko.toast


class WalletTransactionsFragment : Fragment() {
    private lateinit var listView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var message: TextView
    private lateinit var adapter: TransactionAdapter
    private var isLoading: Boolean? = false
    private var isListEmpty: Boolean? = false
    private var list: ArrayList<QrTransaction> = ArrayList()
    private var listOver  = false
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
        val layoutManager = LinearLayoutManager(context)
        listView.layoutManager = layoutManager
        listView.itemAnimator = DefaultItemAnimator()
        listView.setHasFixedSize(true)
        adapter = TransactionAdapter(list)
        listView.addOnScrollListener(ScrollListener {
            if (listOver ) return@ScrollListener

            val lastItemPosition = layoutManager.findLastVisibleItemPosition()
            if (list.size == lastItemPosition + 1) {
                if (lastItemPosition < list.size) {
                    val item = list[list.size - 1]
                    fetchTransactions(item.createdAt)
                }
            }
        })
        listView.adapter = adapter
        isLoading = binding.isLoading
        isListEmpty = binding.isListEmpty
        swipeRefresh.setOnRefreshListener { fetchTransactions() }
        fetchTransactions()
    }

    private fun fetchTransactions(timestamp: String = "") {
        if(isLoading == true) return
        swipeRefresh.isRefreshing = true
        QRTransactionManager.getInstance().getTransactions(timestamp) { response, error ->
            swipeRefresh.isRefreshing = false
            if (error == null) {
                requireContext().toast(error?.message ?: return@getTransactions)
                return@getTransactions
            }
            response?.transactions?.let {
                listOver = it.size < Constants.PAGE_SIZE
                isListEmpty = it.isEmpty()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }
    }
}
