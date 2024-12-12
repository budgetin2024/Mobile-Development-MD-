package com.example.budgee.ui

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.budgee.MainActivity
import com.example.budgee.R
import com.example.budgee.database.Category
import com.example.budgee.database.DatabaseProvider
import com.example.budgee.json.Goal
import com.example.budgee.json.GoalApi
import com.example.budgee.json.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CreateGoalsFragment : Fragment() {

    private lateinit var deadlineEditText: EditText
    private lateinit var goalNameEditText: EditText
    private lateinit var goalAmountEditText: EditText
    private lateinit var createGoalButton: Button
    private lateinit var selectedCategoryTextView: TextView
    private var selectedCategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_goals, container, false)

        // Initialize the EditTexts, Button, and TextView
        deadlineEditText = view.findViewById(R.id.deadlineEditText)
        goalNameEditText = view.findViewById(R.id.goalName)
        goalAmountEditText = view.findViewById(R.id.goalAmount)
        createGoalButton = view.findViewById(R.id.createGoalButton)
        selectedCategoryTextView = view.findViewById(R.id.selectedCategoryTextView)

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

        // Set up category selection on icon click
        setCategoryClickListener(view, R.id.travelingCategory, "Traveling")
        setCategoryClickListener(view, R.id.gadgetCategory, "Gadget")
        setCategoryClickListener(view, R.id.casualCategory, "Casual")
        setCategoryClickListener(view, R.id.fashionCategory, "Fashion")
        setCategoryClickListener(view, R.id.electronicCategory, "Electronic")
        setCategoryClickListener(view, R.id.entertainmentCategory, "Entertainment")
        setCategoryClickListener(view, R.id.propertyCategory, "Properti")

        // Set up button click to create a goal
        createGoalButton.setOnClickListener {
            val goalName = goalNameEditText.text.toString()
            val goalAmount = goalAmountEditText.text.toString().toDoubleOrNull()
            val deadline = deadlineEditText.text.toString()

            if (goalName.isEmpty() || goalAmount == null || deadline.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil currentAmount (misalnya, bisa diambil dari EditText atau nilai lainnya)
            val currentAmount = 0.0  // Misalnya, bisa diambil dari EditText atau data lain

            // Ambil userId dari SharedPreferences atau sumber lain
            val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("auth_token", null)

            if (token == null) {
                Toast.makeText(requireContext(), "Token not found. Please log in again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Membuat objek Goal dengan currentAmount
            val goal = Goal("user_id_example", goalName, goalAmount, currentAmount, deadline)

            // Panggil metode untuk membuat goal dengan token
            createGoal(goal, token)
        }

        // Hide the Bottom Navigation when CreateGoalsFragment is active
        (activity as? MainActivity)?.hideBottomNavigation()

        return view
    }

    // Set listener for category selection on icon click
    private fun setCategoryClickListener(view: View, categoryId: Int, categoryName: String) {
        val categoryIcon = view.findViewById<LinearLayout>(categoryId)
        categoryIcon.setOnClickListener {
            selectedCategory = categoryName
            selectedCategoryTextView.text = "Selected Category: $categoryName"  // Display the selected category
            highlightSelectedCategory(categoryIcon)

            // Save selected category to Room Database
            lifecycleScope.launch {
                val category = Category(name = categoryName)
                val db = DatabaseProvider.getDatabase(requireContext())
                db.categoryDao().insertCategory(category)
                Toast.makeText(requireContext(), "$categoryName saved locally", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Highlight the selected category icon
    private fun highlightSelectedCategory(selectedIcon: LinearLayout) {
        // Reset tint color for all icons
        resetCategoryIcons()

        // Highlight the selected icon
        val selectedImageView = selectedIcon.getChildAt(0) as ImageView
        selectedImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue))  // Change color as needed
    }

    // Reset tint color for all icons
    private fun resetCategoryIcons() {
        val icons = arrayListOf(
            view?.findViewById<LinearLayout>(R.id.travelingCategory),
            view?.findViewById<LinearLayout>(R.id.gadgetCategory),
            view?.findViewById<LinearLayout>(R.id.casualCategory),
            view?.findViewById<LinearLayout>(R.id.fashionCategory),
            view?.findViewById<LinearLayout>(R.id.electronicCategory),
            view?.findViewById<LinearLayout>(R.id.entertainmentCategory),
            view?.findViewById<LinearLayout>(R.id.propertyCategory)
        )

        for (icon in icons) {
            val imageView = icon?.getChildAt(0) as ImageView
            imageView.clearColorFilter()  // Reset color filter
        }
    }

    private fun createGoal(goal: Goal, token: String) {
        // Ambil instance GoalApi dari RetrofitInstance
        val goalApi = RetrofitInstance.goalsRetrofit.create(GoalApi::class.java)

        // Buat permintaan API dengan menambahkan header Authorization dan data goal yang baru
        goalApi.createGoalWithAuth(goal, "Bearer $token").enqueue(object : Callback<Goal> {
            override fun onResponse(call: Call<Goal>, response: Response<Goal>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Goal created successfully", Toast.LENGTH_SHORT).show()
                    Log.d("CreateGoal", "Response successful: ${response.body()}")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Toast.makeText(requireContext(), "Failed to create goal", Toast.LENGTH_SHORT).show()
                    Log.e("CreateGoal", "Error: ${response.message()}")
                    Log.e("CreateGoal", "Error body: $errorBody")
                }
            }

            override fun onFailure(call: Call<Goal>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("CreateGoal", "Failure: ${t.localizedMessage}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show Bottom Navigation again when leaving CreateGoalsFragment
        (activity as? MainActivity)?.showBottomNavigation()
    }
}
