package com.example.budgee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.model.GoalItem

class GoalsAdapter(
    private val items: List<GoalItem>
) : RecyclerView.Adapter<GoalsAdapter.GoalViewHolder>() {

    inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.description)
        val targetValue: TextView = itemView.findViewById(R.id.targetValue)
        val progressBar: ProgressBar = itemView.findViewById(R.id.budgetProgressBar)
        val budgetDetail: TextView = itemView.findViewById(R.id.budgetDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.goals_item, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val item = items[position]
        holder.description.text = item.description
        holder.targetValue.text = item.targetValue
        holder.progressBar.progress = item.progress
        holder.budgetDetail.text = item.budgetDetail
    }

    override fun getItemCount(): Int = items.size
}
