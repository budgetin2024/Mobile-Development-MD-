package com.example.budgee.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.json.TransactionItem
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class TransactionHistoryAdapter(
    private var transactions: List<TransactionItem>
) : RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryIcon: ImageView = view.findViewById(R.id.categoryIcon)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val amountText: TextView = view.findViewById(R.id.amountText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactions[position]
        
        // Set category text
        holder.categoryText.text = transaction.category

        // Set date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(transaction.date)
        holder.dateText.text = outputFormat.format(date)

        // Set amount and color based on type
        val amount = String.format("%,d", transaction.amount)
        holder.amountText.apply {
            text = when (transaction.type) {
                "income" -> {
                    setTextColor(Color.parseColor("#4CAF50"))
                    "+Rp$amount"
                }
                else -> {
                    setTextColor(Color.parseColor("#F44336"))
                    "-Rp$amount"
                }
            }
        }

        // Set icon and background color based on category
        val (icon, color) = when (transaction.category.lowercase()) {
            "food" -> Pair(R.drawable.baseline_fastfood_24, "#FF9800")
            "traveling" -> Pair(R.drawable.baseline_airplanemode_active_24, "#2196F3")
            "shop" -> Pair(R.drawable.baseline_shopping_bag_24, "#9C27B0")
            "gaji" -> Pair(R.drawable.baseline_monetization_on_24, "#4CAF50")
            else -> Pair(R.drawable.baseline_savings_24, "#607D8B")
        }
        
        holder.categoryIcon.apply {
            setImageResource(icon)
            background.setTint(Color.parseColor(color))
        }
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<TransactionItem>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
} 