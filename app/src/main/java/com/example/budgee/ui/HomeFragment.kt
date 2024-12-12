package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.adapter.GoalsAdapter
import com.example.budgee.model.GoalItem
import com.example.budgee.api.TransactionsApi
import com.example.budgee.json.TransactionResponse
import com.example.budgee.model.HomeViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.*
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var goalsRecyclerView: RecyclerView
    private lateinit var goalsAdapter: GoalsAdapter
    private lateinit var profileIcon: ImageView
    private lateinit var seeAllWallets: TextView
    private lateinit var userTextView: TextView
    private lateinit var totalBalanceTextView: TextView

    private lateinit var cardGoals: CardView
    private lateinit var cardStatistics: CardView
    private lateinit var cardIncome: CardView
    private lateinit var cardOutcome: CardView

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize Views
        goalsRecyclerView = view.findViewById(R.id.goalsRecyclerView)
        goalsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        profileIcon = view.findViewById(R.id.profileIcon)
        cardGoals = view.findViewById(R.id.cardViewGoals)
        cardStatistics = view.findViewById(R.id.cardViewStatistic)
        cardIncome = view.findViewById(R.id.cardViewIncome)
        cardOutcome = view.findViewById(R.id.cardViewOutcome)
        seeAllWallets = view.findViewById(R.id.seeAllWallets)
        userTextView = view.findViewById(R.id.user)
        totalBalanceTextView = view.findViewById(R.id.totalBalance)

        // Inisialisasi ViewModel
        viewModel.currentBalance.observe(viewLifecycleOwner, { balance ->
            updateBalanceDisplay(balance)
        })

        // Ambil username yang sudah login dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", AppCompatActivity.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")
        userTextView.text = username

        // Call to get income transactions
        getIncomeTransactions()

        // Call to get outcome transactions
        getOutcomeTransactions()

        // Set click listeners for CardViews
        cardGoals.setOnClickListener { navigateToGoalsDetail() }
        cardStatistics.setOnClickListener { navigateToStatistics() }
        cardIncome.setOnClickListener { navigateToIncome() }
        cardOutcome.setOnClickListener { navigateToOutcome() }

        // Set click listener for See All TextView
        seeAllWallets.setOnClickListener { navigateToGoals() }

        profileIcon.setOnClickListener { navigateToProfile() }

        // Initialize Goals Data
        val goals = listOf(
            GoalItem(description = "Target Pencapaian 1", targetValue = "Rp. 1.000.000.000", progress = 50, budgetDetail = "Rp. 6.600.000 (15 days left)"),
            GoalItem(description = "Target Pencapaian 2", targetValue = "Rp. 500.000.000", progress = 75, budgetDetail = "Rp. 3.300.000 (10 days left)")
        )

        // Set up Adapter
        goalsAdapter = GoalsAdapter(goals)
        goalsRecyclerView.adapter = goalsAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inisialisasi TextView untuk income dan outcome
        val expenseAmountView = view.findViewById<TextView>(R.id.expenseAmount)
        val incomeAmountView = view.findViewById<TextView>(R.id.incomeAmount)

        // Observer untuk income transactions
        viewModel.incomeTransactions.observe(viewLifecycleOwner) { transactions ->
            val totalIncome = transactions.sumOf { it.amount }
            expenseAmountView.text = totalIncome.formatCurrency()
            android.util.Log.d("HomeFragment", "Total Income: $totalIncome")
        }

        // Observer untuk outcome transactions
        viewModel.outcomeTransactions.observe(viewLifecycleOwner) { transactions ->
            val totalOutcome = transactions.sumOf { it.amount }
            incomeAmountView.text = totalOutcome.formatCurrency()
            android.util.Log.d("HomeFragment", "Total Outcome: $totalOutcome")
        }

        // Observer untuk balance tetap ada
        viewModel.currentBalance.observe(viewLifecycleOwner) { balance ->
            updateBalanceDisplay(balance)
        }

        loadAllTransactions()
    }

    private fun loadAllTransactions() {
        android.util.Log.d("HomeFragment", "Starting to load all transactions")
        getIncomeTransactions()
        getOutcomeTransactions()
    }

    private fun getIncomeTransactions() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "Token not found. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend-budgetin.et.r.appspot.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val transactionsApi = retrofit.create(TransactionsApi::class.java)

        transactionsApi.getTransactions(type = "income").enqueue(object : Callback<List<TransactionResponse>> {
            override fun onResponse(call: Call<List<TransactionResponse>>, response: Response<List<TransactionResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    android.util.Log.d("HomeFragment", "Income API response success: ${response.body()?.size} items")
                    viewModel.setIncomeTransactions(response.body()!!)
                } else {
                    android.util.Log.e("HomeFragment", "Income API response failed: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<TransactionResponse>>, t: Throwable) {
                android.util.Log.e("HomeFragment", "Income API call failed: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getOutcomeTransactions() {
        val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "Token not found. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://backend-budgetin.et.r.appspot.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        val transactionsApi = retrofit.create(TransactionsApi::class.java)

        transactionsApi.getTransactions(type = "outcome").enqueue(object : Callback<List<TransactionResponse>> {
            override fun onResponse(call: Call<List<TransactionResponse>>, response: Response<List<TransactionResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    android.util.Log.d("HomeFragment", "Outcome API response success: ${response.body()?.size} items")
                    viewModel.setOutcomeTransactions(response.body()!!)
                } else {
                    android.util.Log.e("HomeFragment", "Outcome API response failed: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<List<TransactionResponse>>, t: Throwable) {
                android.util.Log.e("HomeFragment", "Outcome API call failed: ${t.message}")
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateBalanceDisplay(balance: Double) {
        totalBalanceTextView.text = balance.formatCurrency()
        android.util.Log.d("HomeFragment", "Updated balance display: ${balance.formatCurrency()}")
    }

    private fun Double.formatCurrency(): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(this).replace("IDR", "Rp")
    }

    // Navigation methods (sudah ada di kode Anda)
    private fun navigateToGoalsDetail() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, GoalsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToStatistics() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, StatisticsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToIncome() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, IncomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToOutcome() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, OutcomeFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToGoals() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, GoalsFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun navigateToProfile() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, ProfileFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun refreshData() {
        loadAllTransactions()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCurrentState()
    }
}
