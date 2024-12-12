package com.example.budgee.ui

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.adapter.TransactionAdapter
import com.example.budgee.model.Transaction
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

class StatisticsFragment : Fragment(R.layout.fragment_statistics) {

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var lineChart: LineChart
    private lateinit var selectedMonthTextView: TextView

    private val allTransactions = listOf(
        Transaction(R.drawable.up, "Income", "01/11/2024", "Rp.300.000.000"),
        Transaction(R.drawable.down, "Outcome", "14/11/2024", "Rp.200.000.000"),
        Transaction(R.drawable.up, "Income", "10/10/2024", "Rp.100.000.000")
        // Add more sample transactions
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        selectedMonthTextView = view.findViewById(R.id.selectedMonth)
        transactionsRecyclerView = view.findViewById(R.id.transactionsRecyclerView)

        // Set up RecyclerView
        transactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set initial data
        updateDataForMonth(Calendar.getInstance())

        // Handle month selector click
        view.findViewById<View>(R.id.monthSelector).setOnClickListener {
            showMonthPicker()
        }
    }

    private fun showMonthPicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, 1)
                updateDataForMonth(selectedCalendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.findViewById<View>(
                resources.getIdentifier("day", "id", "android")
            )?.visibility = View.GONE // Hide day picker
        }.show()
    }

    private fun updateDataForMonth(calendar: Calendar) {
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val selectedMonth = monthFormat.format(calendar.time)
        val selectedYear = yearFormat.format(calendar.time)

        // Update selected month TextView
        selectedMonthTextView.text = "$selectedMonth $selectedYear"

        // Filter transactions for the selected month
        val filteredTransactions = allTransactions.filter {
            val transactionDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(it.date)
            val transactionCalendar = Calendar.getInstance()
            transactionCalendar.time = transactionDate
            transactionCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                    transactionCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
        }

        // Update RecyclerView
        transactionAdapter = TransactionAdapter(filteredTransactions)
        transactionsRecyclerView.adapter = transactionAdapter

        // Optionally update the chart
    }

}
