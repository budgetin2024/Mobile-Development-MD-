package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.adapter.GoalsAdapter
import com.example.budgee.model.GoalItem
import androidx.cardview.widget.CardView

class HomeFragment : Fragment() {

    private lateinit var goalsRecyclerView: RecyclerView
    private lateinit var goalsAdapter: GoalsAdapter
    private lateinit var profileIcon: ImageView
    private lateinit var notificationIcon: ImageView
    private lateinit var seeAllWallets: TextView

    // CardViews
    private lateinit var cardGoals: CardView
    private lateinit var cardStatistics: CardView
    private lateinit var cardIncome: CardView
    private lateinit var cardOutcome: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView
        goalsRecyclerView = view.findViewById(R.id.goalsRecyclerView)
        goalsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Initialize Profile and Notification Icons
        profileIcon = view.findViewById(R.id.profileIcon)

        // Initialize CardViews
        cardGoals = view.findViewById(R.id.cardViewGoals)
        cardStatistics = view.findViewById(R.id.cardViewStatistic)
        cardIncome = view.findViewById(R.id.cardViewIncome)
        cardOutcome = view.findViewById(R.id.cardViewOutcome)

        // Initialize See All TextView
        seeAllWallets = view.findViewById(R.id.seeAllWallets)

        // Set click listeners for CardViews
        cardGoals.setOnClickListener {
            navigateToGoalsDetail()
        }

        cardStatistics.setOnClickListener {
            navigateToStatistics()
        }

        cardIncome.setOnClickListener {
            navigateToIncome()
        }

        cardOutcome.setOnClickListener {
            navigateToOutcome()
        }

        // Set click listener for See All TextView
        seeAllWallets.setOnClickListener {
            navigateToGoals()
        }

        profileIcon.setOnClickListener {
            navigateToProfile()
        }

        // Initialize Goals Data
        val goals = listOf(
            GoalItem(
                description = "Target Pencapaian 1",
                targetValue = "Rp. 1.000.000.000",
                progress = 50,
                budgetDetail = "Rp. 6.600.000 (15 days left)"
            ),
            GoalItem(
                description = "Target Pencapaian 2",
                targetValue = "Rp. 500.000.000",
                progress = 75,
                budgetDetail = "Rp. 3.300.000 (10 days left)"
            )
        )

        // Set up Adapter
        goalsAdapter = GoalsAdapter(goals)
        goalsRecyclerView.adapter = goalsAdapter

        return view
    }

    private fun navigateToGoalsDetail() {
        // Aksi saat cardGoals diklik, misalnya berpindah ke GoalsDetailFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, GoalsFragment()) // Ganti ID fragment_container sesuai ID container yang ada
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToStatistics() {
        // Aksi saat cardStatistics diklik, misalnya berpindah ke StatisticsFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, StatisticsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToIncome() {
        // Aksi saat cardIncome diklik, misalnya berpindah ke IncomeFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, IncomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToOutcome() {
        // Aksi saat cardOutcome diklik, misalnya berpindah ke OutcomeFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, OutcomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToGoals() {
        // Aksi saat "See All" diklik, navigasi ke GoalsFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, GoalsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun navigateToProfile() {
        // Aksi saat profileIcon diklik, misalnya berpindah ke ProfileFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, ProfileFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
