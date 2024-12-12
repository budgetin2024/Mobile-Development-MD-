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
import com.example.budgee.MainActivity
import com.example.budgee.R
import com.example.budgee.json.ApiService
import com.example.budgee.model.GoalItem
import com.example.budgee.json.TransactionResponse
import com.example.budgee.json.TransactionsApi
import com.example.budgee.json.TransactionsResponse
import com.example.budgee.json.UserData
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

        // Observe user data
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            userTextView.text = user.name
        }

        // Get user data immediately
        getUserData()

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

        // Panggil getUserData untuk mendapatkan nama user dari API
        getUserData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Inisialisasi TextView untuk income dan outcome
        val expenseAmountView = view.findViewById<TextView>(R.id.expenseAmount)
        val incomeAmountView = view.findViewById<TextView>(R.id.incomeAmount)
        val totalBalanceTextView = view.findViewById<TextView>(R.id.totalBalance)

        // Observer untuk income transactions
        viewModel.incomeTransactions.observe(viewLifecycleOwner) { transactions ->
            val totalIncome = transactions.filter { it.type == "income" }.sumOf { it.amount }
            incomeAmountView.text = totalIncome.formatCurrency()
            android.util.Log.d("HomeFragment", "Total Income Updated: $totalIncome")
        }

        // Observer untuk outcome transactions
        viewModel.outcomeTransactions.observe(viewLifecycleOwner) { transactions ->
            val totalOutcome = transactions.filter { it.type == "expense" }.sumOf { it.amount }
            expenseAmountView.text = totalOutcome.formatCurrency()
            android.util.Log.d("HomeFragment", "Total Outcome Updated: $totalOutcome")
        }

        // Observer untuk balance
        viewModel.currentBalance.observe(viewLifecycleOwner) { balance ->
            totalBalanceTextView.text = balance.formatCurrency()
            android.util.Log.d("HomeFragment", "Current Balance Updated: $balance")
        }

        loadAllTransactions()
    }

    private fun Double.formatCurrency(): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(this).replace("IDR", "Rp")
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

    transactionsApi.getTransactions(type = "income").enqueue(object : Callback<TransactionsResponse> {
        override fun onResponse(call: Call<TransactionsResponse>, response: Response<TransactionsResponse>) {
            if (response.isSuccessful) {
                val transactions = response.body()?.transactions ?: emptyList()
                android.util.Log.d("HomeFragment", "Raw Income Response: ${response.body()}")
                android.util.Log.d("HomeFragment", "Income Transactions Count: ${transactions.size}")
                viewModel.setIncomeTransactions(transactions)
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("HomeFragment", "Income API Failed: ${response.code()}")
                android.util.Log.e("HomeFragment", "Error Body: $errorBody")
                
                // Handle 404 case
                if (response.code() == 404) {
                    viewModel.setIncomeTransactions(emptyList())
                }
            }
        }

        override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {
            android.util.Log.e("HomeFragment", "Income API call failed: ${t.message}")
            viewModel.setIncomeTransactions(emptyList())
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

           transactionsApi.getTransactions(type = "expense").enqueue(object : Callback<TransactionsResponse> {
        override fun onResponse(call: Call<TransactionsResponse>, response: Response<TransactionsResponse>) {
            if (response.isSuccessful) {
                val transactions = response.body()?.transactions ?: emptyList()
                android.util.Log.d("HomeFragment", "Raw Outcome Response: ${response.body()}")
                android.util.Log.d("HomeFragment", "Outcome Transactions Count: ${transactions.size}")
                viewModel.setOutcomeTransactions(transactions)
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("HomeFragment", "Outcome API Failed: ${response.code()}")
                android.util.Log.e("HomeFragment", "Error Body: $errorBody")
                
                // Handle 404 case
                if (response.code() == 404) {
                    viewModel.setOutcomeTransactions(emptyList())
                }
            }
        }

        override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {
            android.util.Log.e("HomeFragment", "Outcome API call failed: ${t.message}")
            viewModel.setOutcomeTransactions(emptyList())
        }
    })
}
    
    override fun onResume() {
        super.onResume()
        loadAllTransactions() // Memastikan data diload ulang saat fragment di-resume
    }

    private fun updateBalanceDisplay(balance: Double) {
        totalBalanceTextView.text = balance.formatCurrency()
        android.util.Log.d("HomeFragment", "Balance Display Updated: ${balance.formatCurrency()}")
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

   private fun getUserData() {
    val sharedPreferences = requireActivity().getSharedPreferences("app_prefs", AppCompatActivity.MODE_PRIVATE)
    val token = sharedPreferences.getString("auth_token", null)

    if (token.isNullOrEmpty()) {
        Toast.makeText(context, "Token tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        return
    }

    // Tambahkan logging untuk melihat token
    android.util.Log.d("HomeFragment", "Token from SharedPreferences: $token")

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "Bearer $token") // Tambahkan "Bearer " prefix
                .build()
            chain.proceed(request)
        }
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://backend-budgetin.et.r.appspot.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    // Gunakan token dengan prefix "Bearer "
     apiService.getUserData("Bearer $token").enqueue(object : Callback<UserData> {
        override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
            android.util.Log.d("HomeFragment", "URL Request: ${call.request().url}")
            android.util.Log.d("HomeFragment", "Headers: ${call.request().headers}")
            android.util.Log.d("HomeFragment", "Response code: ${response.code()}")
            
            if (response.isSuccessful) {
                val userData = response.body()
                android.util.Log.d("HomeFragment", "Response body: $userData")
                
                userData?.user?.let {
                    activity?.runOnUiThread {
                        userTextView.text = it.name
                        android.util.Log.d("HomeFragment", "Nama user diset: ${it.name}")
                        
                        // Simpan nama user ke SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("user_name", it.name)
                        editor.apply()
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("HomeFragment", "Error response: $errorBody")
                
                // Tambahkan pengecekan token expired
                if (response.code() == 401) {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Sesi telah berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
                        // Hapus token dan redirect ke login
                        sharedPreferences.edit().remove("auth_token").apply()
                        // Redirect ke login screen
                        (activity as? MainActivity)?.replaceFragmentInActivity(LoginFragment())
                    }
                }
            }
        }

        override fun onFailure(call: Call<UserData>, t: Throwable) {
            android.util.Log.e("HomeFragment", "Network error: ${t.message}")
            activity?.runOnUiThread {
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        }
    })
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.initializeFromSharedPreferences(requireContext())
    }
}
