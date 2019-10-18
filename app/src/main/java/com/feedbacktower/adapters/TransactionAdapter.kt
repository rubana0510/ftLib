package com.feedbacktower.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feedbacktower.data.models.QrTransaction
import com.feedbacktower.databinding.ItemWalletTransactionBinding

/**
 * Created by sanket on 16-04-2019.
 */
class TransactionAdapter(private val list: List<QrTransaction>) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemWalletTransactionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(View.OnClickListener {

        }, item)
    }

    private fun getItem(position: Int): QrTransaction {
        return list[position]
    }

    fun getItemAtPos(position: Int): QrTransaction = getItem(position)

    class ViewHolder(
        val binding: ItemWalletTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listnr: View.OnClickListener, item: QrTransaction) {
            binding.apply {
                listener = listnr
                transaction = item
                executePendingBindings()
            }
        }
    }
}