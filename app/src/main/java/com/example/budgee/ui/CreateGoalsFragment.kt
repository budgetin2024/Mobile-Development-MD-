package com.example.budgee.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.budgee.R
import com.example.budgee.MainActivity
import java.util.Calendar

class CreateGoalsFragment : Fragment() {

    private lateinit var deadlineEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_goals, container, false)

        // Initialize the EditText for deadline
        deadlineEditText = view.findViewById(R.id.deadlineEditText)

        // Set the click listener to show DatePickerDialog
        deadlineEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Create the DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Set the selected date to the EditText in dd/MM/yyyy format
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    deadlineEditText.setText(date)
                },
                year, month, day
            )

            datePickerDialog.show()
        }

        // Hide the Bottom Navigation when CreateGoalsFragment is active
        (activity as? MainActivity)?.hideBottomNavigation()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show Bottom Navigation again when leaving CreateGoalsFragment
        (activity as? MainActivity)?.showBottomNavigation()
    }
}
