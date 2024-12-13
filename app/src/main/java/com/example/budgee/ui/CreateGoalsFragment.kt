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
import com.example.budgee.MainActivity
import com.example.budgee.R
import com.example.budgee.json.GoalApi
import com.example.budgee.json.GoalRequest
import com.example.budgee.json.GoalsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.appcompat.app.AppCompatActivity
import com.example.budgee.json.Goal

class CreateGoalsFragment : Fragment() {

    private val BASE_URL = "https://backend-budgetin.et.r.appspot.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val goalApi = retrofit.create(GoalApi::class.java)

    private lateinit var deadlineEditText: EditText
    private lateinit var goalAmountEditText: EditText
    private lateinit var createGoalButton: Button
    private lateinit var selectedCategoryTextView: TextView
    private var selectedCategory: String? = null
    private var selectedDate: String? = null
    private lateinit var goalNameEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_goals, container, false)

        // Sembunyikan Bottom Navigation saat fragment dibuat
        (activity as? MainActivity)?.hideBottomNavigation()

        // Initialize the EditTexts, Button, and TextView
        deadlineEditText = view.findViewById(R.id.deadlineEditText)
        goalAmountEditText = view.findViewById(R.id.goalAmount)
        createGoalButton = view.findViewById(R.id.createGoalButton)
        selectedCategoryTextView = view.findViewById(R.id.selectedCategoryTextView)
        goalNameEditText = view.findViewById(R.id.goalNameEditText)

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
                    selectedDate = date
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
            createGoal()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Pastikan bottom navigation tetap tersembunyi saat fragment di-resume
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    // Set listener for category selection on icon click
    private fun setCategoryClickListener(view: View, categoryId: Int, categoryName: String) {
        val categoryIcon = view.findViewById<LinearLayout>(categoryId)
        categoryIcon.setOnClickListener {
            selectedCategory = categoryName
            selectedCategoryTextView.text = "Selected Category: $categoryName"
            highlightSelectedCategory(categoryIcon)
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

    private fun getToken(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi back icon
        val backIcon = view.findViewById<ImageView>(R.id.backIcon)

        // Set click listener untuk back icon
        backIcon.setOnClickListener {
            // Kembali ke HomeFragment
            (activity as? MainActivity)?.apply {
                replaceFragmentInActivity(GoalsFragment(), false) // false agar tidak ditambahkan ke back stack
                showBottomNavigation() // Pastikan bottom navigation muncul
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Tampilkan kembali Bottom Navigation saat meninggalkan fragment
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    private fun createGoal() {
        val goalAmount = goalAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
        val deadline = selectedDate ?: "" // pastikan selectedDate tidak null

        // Validasi input
        if (goalAmount <= 0) {
            goalAmountEditText.error = "Jumlah goal harus lebih dari 0"
            return
        }
        if (deadline.isEmpty()) {
            Toast.makeText(context, "Pilih tanggal deadline", Toast.LENGTH_SHORT).show()
            return
        }

        val token = getToken()
        if (token != null) {
            createGoal(token)
        }
    }

    private fun createGoal(token: String) {
        val goalAmount = try {
            goalAmountEditText.text.toString().toBigDecimal().toDouble()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Jumlah tidak valid", Toast.LENGTH_SHORT).show()
            return
        }
        
        val goalName = goalNameEditText.text.toString().trim()
        val deadline = deadlineEditText.text.toString()
        
        if (goalAmount <= 0 || deadline.isEmpty() || selectedCategory == null || goalName.isEmpty()) {
            Toast.makeText(requireContext(), "Mohon lengkapi semua field", Toast.LENGTH_SHORT).show()
            return
        }

        // Format tanggal
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        val date = inputFormat.parse(deadline)
        val formattedDate = date?.let { outputFormat.format(it) } ?: return

        // Buat request dengan format yang baru
        val goalRequest = GoalRequest(
            goalName = goalName,
            goalAmount = goalAmount,
            currentAmount = 0.0,
            deadline = formattedDate,
            category = selectedCategory!!.lowercase()
        )

        // Log request yang akan dikirim
        Log.d("CreateGoal", """
            Request Data:
            {
                "goalName": "${goalRequest.goalName}",
                "goalAmount": ${goalRequest.goalAmount},
                "currentAmount": ${goalRequest.currentAmount},
                "deadline": "${goalRequest.deadline}",
                "category": "${goalRequest.category}"
            }
        """.trimIndent())

        // Kirim request
        goalApi.createGoalWithAuth(goalRequest, "Bearer $token").enqueue(object : Callback<Goal> {
            override fun onResponse(call: Call<Goal>, response: Response<Goal>) {
                // Cek apakah fragment masih attached
                if (!isAdded) return

                if (response.isSuccessful) {
                    val createdGoal = response.body()
                    Log.d("CreateGoal", "Success Response: $createdGoal")
                    
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Goal berhasil dibuat", Toast.LENGTH_SHORT).show()
                        
                        // Refresh goals list di GoalsFragment jika masih attached
                        (parentFragment as? GoalsFragment)?.refreshGoals()
                        
                        // Kembali ke fragment sebelumnya
                        activity?.supportFragmentManager?.popBackStack()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CreateGoal", "Error Response: ${response.code()}")
                    Log.e("CreateGoal", "Error Body: $errorBody")
                    
                    activity?.runOnUiThread {
                        when {
                            errorBody?.contains("same name already exists") == true -> {
                                Toast.makeText(context, "Goal dengan nama yang sama sudah ada", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(context, "Gagal membuat goal: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Goal>, t: Throwable) {
                // Cek apakah fragment masih attached
                if (!isAdded) return
                
                Log.e("CreateGoal", "Network Error: ${t.message}")
                activity?.runOnUiThread {
                    Toast.makeText(context, "Error koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
