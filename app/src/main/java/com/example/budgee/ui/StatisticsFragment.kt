package com.example.budgee.ui

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.adapter.TransactionAdapter
import com.example.budgee.adapter.TransactionHistoryAdapter
import com.example.budgee.json.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private lateinit var transactionHistoryAdapter: TransactionHistoryAdapter
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var lineChart: LineChart
    private lateinit var selectedMonthTextView: TextView
    private lateinit var predictionTextView: TextView
    private lateinit var predictionChart: LineChart
    private var allTransactions: List<TransactionItem> = listOf()
    private lateinit var transactionsApi: TransactionsApi
    private lateinit var monthSelector: LinearLayout
    private var selectedCalendar = Calendar.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi adapter
        transactionHistoryAdapter = TransactionHistoryAdapter(emptyList())
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)
        
        // Tambahkan LayoutManager
        transactionsRecyclerView.layoutManager = LinearLayoutManager(context)
        transactionsRecyclerView.adapter = transactionHistoryAdapter
        
        // Ambil token dari SharedPreferences
        val sharedPrefs = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPrefs?.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "Token not found. Please login again.", Toast.LENGTH_SHORT).show()
            return
        }

        // Setup Retrofit dengan Authorization Token
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

        transactionsApi = retrofit.create(TransactionsApi::class.java)
        
        monthSelector = view.findViewById(R.id.monthSelector)
        selectedMonthTextView = view.findViewById(R.id.selectedMonth)
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)
        predictionTextView = view.findViewById(R.id.predictionTextView)
        predictionChart = view.findViewById(R.id.predictionChart)

        // Set bulan saat ini sebagai default
        updateSelectedMonthText(selectedCalendar)

        // Setup click listener untuk monthSelector
        monthSelector.setOnClickListener {
            showMonthYearPicker()
        }

        setupPredictionChart()
        fetchTransactions()
    }

    private fun showMonthYearPicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                updateSelectedMonthText(selectedCalendar)
                filterTransactionsForSelectedMonth()
            },
            selectedCalendar.get(Calendar.YEAR),
            selectedCalendar.get(Calendar.MONTH),
            1
        )

        // Sembunyikan picker tanggal karena kita hanya butuh bulan dan tahun
        datePickerDialog.datePicker.findViewById<View>(
            resources.getIdentifier("day", "id", "android")
        )?.visibility = View.GONE

        datePickerDialog.show()
    }

    private fun updateSelectedMonthText(calendar: Calendar) {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("id", "ID"))
        selectedMonthTextView.text = monthFormat.format(calendar.time).capitalize()
    }

    private fun filterTransactionsForSelectedMonth() {
        val filteredTransactions = allTransactions.filter { transaction ->
            val transactionDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .parse(transaction.date)

            val transactionCalendar = Calendar.getInstance().apply {
                time = transactionDate
            }

            transactionCalendar.get(Calendar.MONTH) == selectedCalendar.get(Calendar.MONTH) &&
            transactionCalendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR)
        }

        // Update RecyclerView dengan transaksi yang difilter
        updateTransactionsList(filteredTransactions)
        
        // Update prediksi berdasarkan data yang difilter
        updatePredictionData()
    }

    private fun updateTransactionsList(transactions: List<TransactionItem>) {
        val sortedTransactions = transactions.sortedByDescending { it.date }
        activity?.runOnUiThread {
            transactionHistoryAdapter.updateTransactions(sortedTransactions)  // Di sini fungsinya dipanggil
        }
    }

    private fun setupPredictionChart() {
        predictionChart.apply {
            description.isEnabled = true
            description.textSize = 14f
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val calendar = Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_MONTH, value.toInt() - 3)
                        return SimpleDateFormat("dd/MM", Locale.getDefault()).format(calendar.time)
                    }
                }
                textSize = 12f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "Rp${String.format("%,.0f", value)}"
                    }
                }
                textSize = 12f
            }
            axisRight.isEnabled = false

            legend.apply {
                textSize = 12f
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            setExtraOffsets(10f, 10f, 10f, 10f)
        }
    }

    private fun updatePredictionData() {
        val last3DaysData = calculateLast3DaysExpenses()
        val todayPrediction = predictTodayExpense(last3DaysData)
        
        val entries = mutableListOf<Entry>()
        
        // Data 3 hari terakhir
        last3DaysData.forEachIndexed { index, value ->
            entries.add(Entry(index.toFloat(), value.toFloat()))
        }
        
        // Prediksi hari ini
        entries.add(Entry(3f, todayPrediction.toFloat()))

        val dataSet = LineDataSet(entries, "Pengeluaran & Prediksi Hari Ini").apply {
            color = Color.BLUE
            setCircleColor(Color.BLUE)
            lineWidth = 2f
            circleRadius = 4f
            setDrawValues(true)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "Rp${String.format("%,.0f", value)}"
                }
            }
        }

        predictionChart.apply {
            data = LineData(dataSet)
            description.text = "Prediksi Pengeluaran Hari Ini"
            animateX(1000)
            invalidate()
        }

        // Update text prediksi
        predictionTextView.text = "Prediksi pengeluaran hari ini:\nRp${String.format("%,.0f", todayPrediction)}"
    }

    private fun calculateLast3DaysExpenses(): List<Double> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -3) // Mundur 3 hari
        val threeDaysAgo = calendar.time

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        return allTransactions
            .filter { transaction -> 
                val transactionDate = dateFormat.parse(transaction.date)
                transactionDate?.after(threeDaysAgo) == true && 
                transaction.type == "expense"
            }
            .groupBy { 
                val date = dateFormat.parse(it.date)
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            }
            .mapValues { (_, transactions) ->
                transactions.sumOf { it.amount.toDouble() }
            }
            .toSortedMap()
            .values
            .toList()
    }

    private fun predictTodayExpense(last3Days: List<Double>): Double {
        if (last3Days.isEmpty()) return 0.0

        // Menghitung rata-rata
        val average = last3Days.average()
        
        // Menghitung trend (kecenderungan naik/turun)
        val trend = if (last3Days.size >= 2) {
            var totalChange = 0.0
            for (i in 1 until last3Days.size) {
                totalChange += last3Days[i] - last3Days[i-1]
            }
            totalChange / (last3Days.size - 1)
        } else {
            0.0
        }

        // Prediksi = rata-rata + trend
        return maxOf(0.0, average + trend)
    }

    private fun fetchTransactions() {
        transactionsApi.getTransactions(null).enqueue(object : Callback<TransactionsResponse> {
            override fun onResponse(
                call: Call<TransactionsResponse>,
                response: Response<TransactionsResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { transactionsResponse ->
                        val transactions = transactionsResponse.transactions.map { transaction ->
                            TransactionItem(
                                _id = transaction._id,
                                userId = transaction.userId,
                                type = transaction.type,
                                category = transaction.category,
                                amount = transaction.amount.toInt(),
                                date = transaction.date
                            )
                        }
                        allTransactions = transactions
                        filterTransactionsForSelectedMonth() // Filter untuk bulan yang dipilih
                    }
                }
            }

            override fun onFailure(call: Call<TransactionsResponse>, t: Throwable) {
                Log.e("StatisticsFragment", "Error: ${t.message}")
            }
        })
    }

}
