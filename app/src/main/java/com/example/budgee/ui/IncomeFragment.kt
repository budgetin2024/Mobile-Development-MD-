package com.example.budgee.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgee.MainActivity
import com.example.budgee.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class IncomeFragment : Fragment(), Category.CategorySelectionListener {

    private lateinit var amountEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var dateValueTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var backIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_income, container, false)

        // Hide Bottom Navigation when IncomeFragment is active
        (activity as? MainActivity)?.hideBottomNavigation()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Views
        amountEditText = view.findViewById(R.id.amountEditText)
        dateTextView = view.findViewById(R.id.dateTextView)
        dateValueTextView = view.findViewById(R.id.dateValueTextView)
        categoryTextView = view.findViewById(R.id.categoryTextView)
        timeTextView = view.findViewById(R.id.timeTextView)
        backIcon = view.findViewById(R.id.backIcon)

        // Handle back icon click
        backIcon.setOnClickListener {
            // Navigate directly to the HomeFragment
            replaceFragment(HomeFragment())
        }


        // Set current date as default
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        dateValueTextView.text = currentDate

        // Handle date click to show DatePickerDialog
        dateValueTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                    dateValueTextView.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Handle category click to show CategoryBottomSheet
        categoryTextView.setOnClickListener {
            val categoryBottomSheet = Category()
            categoryBottomSheet.setCategorySelectionListener(this, showFoodTravelingShopMedicalInvestmentTransportationAnother = false)
            categoryBottomSheet.show(parentFragmentManager, categoryBottomSheet.tag)
        }

        // Handle time click to show TimePickerDialog
        timeTextView.setOnClickListener {
            showTimePicker()
        }

        // Handle save button click
        view.findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            val amountText = amountEditText.text.toString().trim()
            if (amountText.isEmpty()) {
                Toast.makeText(context, "Please enter an income amount", Toast.LENGTH_SHORT).show()
            } else {
                // Add logic to save the income data
                Toast.makeText(context, "Income saved successfully", Toast.LENGTH_SHORT).show()
                // Go back to the previous fragment
                parentFragmentManager.popBackStack()
            }
        }
    }

    // Helper function to replace fragments
    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        // Replace the current fragment with the specified fragment (HomeFragment in this case)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)  // Replace with your fragment container ID
            .addToBackStack(null)  // Optional: Add this fragment to the back stack if you want to allow back navigation
            .commit()
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
    override fun onCategorySelected(category: String) {
        categoryTextView.text = category
    }

    // Show Bottom Navigation when leaving the fragment
    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNavigation()
    }
}
