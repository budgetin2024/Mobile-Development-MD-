package com.example.budgee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.model.Wallet

class WalletAdapter(private val walletList: List<Wallet>) :
    RecyclerView.Adapter<WalletAdapter.WalletViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wallet_item, parent, false)
        return WalletViewHolder(view)
    }

    override fun onBindViewHolder(holder: WalletViewHolder, position: Int) {
        val wallet = walletList[position]
        holder.walletBalanceLabel.text = "Total Balance"
        holder.walletBalanceValue.text = wallet.balance
        holder.walletDetail.text = wallet.detail
        holder.walletIcon.setImageResource(wallet.icon)
    }

    override fun getItemCount(): Int {
        return walletList.size
    }

    class WalletViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val walletBalanceLabel: TextView = itemView.findViewById(R.id.walletBalanceLabel)
        val walletBalanceValue: TextView = itemView.findViewById(R.id.walletBalanceValue)
        val walletDetail: TextView = itemView.findViewById(R.id.walletDetail)
        val walletIcon: ImageView = itemView.findViewById(R.id.walletIcon)
    }
}
