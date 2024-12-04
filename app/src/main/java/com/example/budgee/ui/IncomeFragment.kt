package com.example.budgee.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.budgee.R
import java.text.SimpleDateFormat
import java.util.*

class IncomeFragment : Fragment() {

    private lateinit var amountEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var dateValueTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var timeTextView: TextView  // Add time TextView reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_income, container, false)

        // Initialize Views
        amountEditText = view.findViewById(R.id.amountEditText)
        dateTextView = view.findViewById(R.id.dateTextView)
        dateValueTextView = view.findViewById(R.id.dateValueTextView)
        categoryTextView = view.findViewById(R.id.categoryTextView)
        timeTextView = view.findViewById(R.id.timeTextView)  // Initialize time TextView

        // Set current date as default
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // Format the current date to display it on dateValueTextView
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        dateValueTextView.text = currentDate

        // Handle date click to show DatePickerDialog
        dateValueTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year1, monthOfYear, dayOfMonth1 ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year1, monthOfYear, dayOfMonth1)
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                    dateValueTextView.text = formattedDate
                },
                year,
                month,
                dayOfMonth
            )
            datePickerDialog.show()
        }

        // Handle category click to show CategoryBottomSheet
        categoryTextView.setOnClickListener {
            val categoryBottomSheet = CategoryBottomSheetFragment()
            categoryBottomSheet.show(parentFragmentManager, categoryBottomSheet.tag)
        }

        // Handle time click to show TimePickerDialog
        timeTextView.setOnClickListener {
            showTimePicker()
        }

        return view
    }

    // Show TimePickerDialog when Time TextView is clicked
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                // Update the Time TextView with the selected time
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                timeTextView.text = formattedTime
            },
            hour,
            minute,
            true // Use 24-hour format
        )
        timePickerDialog.show()
    }

    // Update category when selected from CategoryBottomSheet
    fun updateCategory(category: String) {
        categoryTextView.text = category
    }
}
