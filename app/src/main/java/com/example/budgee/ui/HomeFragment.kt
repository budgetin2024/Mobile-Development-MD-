package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.adapter.TransactionAdapter
import com.example.budgee.adapter.WalletAdapter
import com.example.budgee.model.Transaction
import com.example.budgee.model.Wallet
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi DrawerLayout dan NavigationView
        drawerLayout = view.findViewById(R.id.drawerLayout)
        navigationView = view.findViewById(R.id.navigationView)

        // Inisialisasi menu icon
        val menuIcon: ImageView = view.findViewById(R.id.menuIcon)
        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(navigationView)
        }

        // Tangani klik item di Navigation Drawer
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_payment -> Toast.makeText(requireContext(), "Payment clicked", Toast.LENGTH_SHORT).show()
                R.id.menu_statistics -> Toast.makeText(requireContext(), "Statistics clicked", Toast.LENGTH_SHORT).show()
                R.id.menu_invest -> Toast.makeText(requireContext(), "Invest clicked", Toast.LENGTH_SHORT).show()
                R.id.menu_news -> Toast.makeText(requireContext(), "News clicked", Toast.LENGTH_SHORT).show()
                R.id.menu_account -> Toast.makeText(requireContext(), "Account clicked", Toast.LENGTH_SHORT).show()
            }
            drawerLayout.closeDrawer(navigationView)
            true
        }

        // Inisialisasi RecyclerView untuk Wallet
        val walletsRecyclerView: RecyclerView = view.findViewById(R.id.walletsRecyclerView)
        val walletList = listOf(
            Wallet("Rp. 470.000.000", "**** 3456", R.drawable.ic_wallet_blue),
            Wallet("Rp. 50.000.000", "PAYO", R.drawable.ic_wallet_green)
        )
        val walletAdapter = WalletAdapter(walletList)
        walletsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        walletsRecyclerView.adapter = walletAdapter

        // Inisialisasi RecyclerView untuk Transactions
        val transactionsRecyclerView: RecyclerView = view.findViewById(R.id.transactionsRecyclerView)
        val transactionList = listOf(
            Transaction("Travel", "04-11-2024", "-Rp. 6.000.000", R.drawable.pesawat),
            Transaction("Food", "04-11-2024", "-Rp. 150.000", R.drawable.food),
            Transaction("Shopping", "03-11-2024", "-Rp. 1.300.000", R.drawable.shoping),
            Transaction("Shopping", "03-11-2024", "-Rp. 2.100.000", R.drawable.shoping)
        )
        val transactionAdapter = TransactionAdapter(transactionList)
        transactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        transactionsRecyclerView.adapter = transactionAdapter
    }

}
