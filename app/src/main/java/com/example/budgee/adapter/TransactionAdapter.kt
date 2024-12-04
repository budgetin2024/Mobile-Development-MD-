package com.example.budgee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.model.Transaction

class TransactionAdapter(private val transactionList: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.transactionIcon.setImageResource(transaction.imageRes)
        holder.transactionCategory.text = transaction.title
        holder.transactionDate.text = transaction.date
        holder.transactionAmount.text = transaction.amount
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val transactionIcon: ImageView = itemView.findViewById(R.id.transactionIcon)
        val transactionCategory: TextView = itemView.findViewById(R.id.transactionCategory)
        val transactionDate: TextView = itemView.findViewById(R.id.transactionDate)
        val transactionAmount: TextView = itemView.findViewById(R.id.transactionAmount)
    }
}
