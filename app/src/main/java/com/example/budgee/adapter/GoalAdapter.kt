package com.example.budgee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.json.Goal
import java.text.NumberFormat
import java.util.*

class GoalAdapter(
    private var goals: List<Goal>,
    private val onDeleteClick: ((Goal) -> Unit)? = null,
    private val onProgressClick: ((Goal) -> Unit)? = null
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    class GoalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val goalName: TextView = view.findViewById(R.id.goalNameText)
        val categoryIcon: ImageView = view.findViewById(R.id.categoryIcon)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val duedate: TextView = view.findViewById(R.id.deadlineText)
        val totalAmount: TextView = view.findViewById(R.id.totalAmountText)
        val currentAmount: TextView = view.findViewById(R.id.currentAmountText)
        val progressText: TextView = view.findViewById(R.id.progressText)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]

        // Format currency
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        val targetAmount = formatter.format(goal.targetAmount)
        val currentAmount = formatter.format(goal.progress)

        // Update views
        holder.goalName.text = goal.goalName ?: "Goal Tanpa Nama"
        holder.totalAmount.text = "Target: $targetAmount"
        holder.currentAmount.text = "Progress: $currentAmount"
        holder.progressText.text = "${goal.progress.toInt()}%"
        holder.duedate.text = goal.duedate

        // Set category icon dan text
        when (goal.category?.lowercase()) {
            "entertaiment" -> holder.categoryIcon.setImageResource(R.drawable.baseline_kitesurfing_24)
            "shop" -> holder.categoryIcon.setImageResource(R.drawable.baseline_shopping_bag_24)
            "travel" -> holder.categoryIcon.setImageResource(R.drawable.baseline_airplanemode_active_24)
            else -> holder.categoryIcon.setImageResource(R.drawable.baseline_monetization_on_24)
        }
        holder.categoryText.text = goal.category ?: "Lainnya"

        // Set click listener untuk delete
        holder.deleteButton.setOnClickListener {
            onDeleteClick?.invoke(goal)
        }

        // Set click listener untuk update current amount
        holder.itemView.setOnClickListener {
            onProgressClick?.invoke(goal)
        }

        // Optional: Tambahkan animasi progress atau visual feedback
        holder.itemView.alpha = if (goal.progress >= 100) 0.7f else 1.0f
    }

    override fun getItemCount() = goals.size

    fun getGoals(): List<Goal> {
        return goals
    }

    fun updateGoals(newGoals: List<Goal>) {
        goals = newGoals
        notifyDataSetChanged()
    }
}