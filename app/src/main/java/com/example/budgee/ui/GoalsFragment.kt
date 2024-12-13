package com.example.budgee.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budgee.R
import com.example.budgee.MainActivity
import com.example.budgee.adapter.GoalAdapter
import com.example.budgee.json.Goal
import com.example.budgee.json.GoalsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.budgee.json.GoalApi
import com.example.budgee.json.ProgressRequest
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Locale
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.Header

class GoalsFragment : Fragment() {

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

    private lateinit var createGoalButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var goalAdapter: GoalAdapter
    private lateinit var totalGoalsCount: TextView

    private interface GoalsApiService {
        @PUT("goals/{id}")
        fun updateGoal(
            @Path("id") goalId: String,
            @Header("Authorization") token: String,
            @Body goal: GoalUpdateRequest
        ): Call<GoalResponse>

        @DELETE("goals/{id}")
        fun deleteGoal(
            @Path("id") goalId: String,
            @Header("Authorization") token: String
        ): Call<Void>
    }

    // Data class untuk request update goal
    data class GoalUpdateRequest(
        val name: String,
        val targetAmount: Double,
        val deadline: String,
        val currentAmount: Double
    )

    // Data class untuk response
    data class GoalResponse(
        val message: String,
        val goal: Goal
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goals, container, false)

        // Hide bottom navigation
        (activity as? MainActivity)?.hideBottomNavigation()

        // Initialize views
        totalGoalsCount = view.findViewById(R.id.totalGoalsCount)
        recyclerView = view.findViewById(R.id.goalsRecyclerView)
        createGoalButton = view.findViewById(R.id.createGoalButton)

        // Setup RecyclerView tanpa listener
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            goalAdapter = GoalAdapter(
                goals = emptyList(),
                onDeleteClick = { goal ->
                    showDeleteConfirmationDialog(goal)
                },
                onProgressClick = { goal ->
                    showUpdateCurrentAmountDialog(goal)
                }
            )
            adapter = goalAdapter
            visibility = View.VISIBLE
        }

        // Debug log
        Log.d("GoalsFragment", "RecyclerView setup completed")

        createGoalButton.setOnClickListener {
            navigateToCreateGoal()
        }

        // Load goals
        loadGoals()

        return view
    }

    private fun loadGoals() {
        val token = getToken()
        if (token != null) {
            Log.d("GoalsFragment", "Loading goals with token: ${token.take(10)}...")
            
            goalApi.getGoals("Bearer $token").enqueue(object : Callback<GoalsResponse> {
                override fun onResponse(call: Call<GoalsResponse>, response: Response<GoalsResponse>) {
                    Log.d("GoalsFragment", "Response code: ${response.code()}")
                    
                    if (response.isSuccessful) {
                        val goals = response.body()?.goals
                        Log.d("GoalsFragment", "Raw response: ${response.body()}")
                        Log.d("GoalsFragment", "Parsed goals: $goals")
                        
                        activity?.runOnUiThread {
                            if (goals != null && goals.isNotEmpty()) {
                                totalGoalsCount.text = "${goals.size}"
                                goalAdapter.updateGoals(goals)
                                recyclerView.visibility = View.VISIBLE
                                Log.d("GoalsFragment", "Updated adapter with ${goals.size} goals")
                            } else {
                                Log.d("GoalsFragment", "No goals found")
                                totalGoalsCount.text = "0"
                                recyclerView.visibility = View.GONE
                            }
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("GoalsFragment", "Error Response: ${response.code()}")
                        Log.e("GoalsFragment", "Error Body: $errorBody")
                        
                        activity?.runOnUiThread {
                            Toast.makeText(context, "Gagal memuat goals: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GoalsResponse>, t: Throwable) {
                    Log.e("GoalsFragment", "Network Error: ${t.message}")
                    Log.e("GoalsFragment", "Stack trace:", t)
                    
                    activity?.runOnUiThread {
                        Toast.makeText(context, "Error koneksi: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        } else {
            Log.e("GoalsFragment", "Token is null")
            Toast.makeText(context, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getToken(): String? {
        return context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            ?.getString("auth_token", null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi back icon
        val backIcon = view.findViewById<ImageView>(R.id.backIcon)
        
        // Set click listener untuk back icon
        backIcon.setOnClickListener {
            // Kembali ke HomeFragment
            (activity as? MainActivity)?.apply {
                replaceFragmentInActivity(HomeFragment(), false) // false agar tidak ditambahkan ke back stack
                showBottomNavigation() // Pastikan bottom navigation muncul
            }
        }
    }

    private fun navigateToCreateGoal() {
        // Aksi saat tombol "Create Goal" diklik, berpindah ke CreateGoalsFragment
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, CreateGoalsFragment()) // Ganti dengan fragment container ID Anda
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show bottom navigation when this fragment is destroyed
        (activity as? MainActivity)?.showBottomNavigation()
    }

    fun refreshGoals() {
        loadGoals()
    }

    private fun updateGoal(goalId: String, name: String, targetAmount: Double, deadline: String) {
        val token = getToken() ?: return
        
        val updateRequest = GoalUpdateRequest(name, targetAmount, deadline, 0.0)
        
        val retrofit = setupRetrofit()
        val goalsApi = retrofit.create(GoalsApiService::class.java)

        goalsApi.updateGoal(goalId, "Bearer $token", updateRequest)
            .enqueue(object : Callback<GoalResponse> {
                override fun onResponse(call: Call<GoalResponse>, response: Response<GoalResponse>) {
                    activity?.runOnUiThread {
                        when (response.code()) {
                            200 -> {
                                Toast.makeText(context, "Goal berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                // Refresh data goals
                                loadGoals()
                            }
                            401 -> handleAuthError()
                            else -> {
                                val errorBody = response.errorBody()?.string()
                                Log.e("GoalsFragment", "Update error: $errorBody")
                                Toast.makeText(context, "Gagal memperbarui goal", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GoalResponse>, t: Throwable) {
                    activity?.runOnUiThread {
                        Log.e("GoalsFragment", "Update network error: ${t.message}")
                        Toast.makeText(context, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun deleteGoal(goalId: String) {
        val token = getToken() ?: return
        
        val retrofit = setupRetrofit()
        val goalsApi = retrofit.create(GoalsApiService::class.java)

        goalsApi.deleteGoal(goalId, "Bearer $token")
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    activity?.runOnUiThread {
                        when (response.code()) {
                            200, 204 -> {
                                Toast.makeText(context, "Goal berhasil dihapus", Toast.LENGTH_SHORT).show()
                                // Refresh data goals
                                loadGoals()
                            }
                            401 -> handleAuthError()
                            else -> {
                                val errorBody = response.errorBody()?.string()
                                Log.e("GoalsFragment", "Delete error: $errorBody")
                                Toast.makeText(context, "Gagal menghapus goal", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    activity?.runOnUiThread {
                        Log.e("GoalsFragment", "Delete network error: ${t.message}")
                        Toast.makeText(context, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun handleAuthError() {
        Toast.makeText(context, "Sesi telah berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
        context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            ?.edit()?.remove("auth_token")?.apply()
        (activity as? MainActivity)?.replaceFragmentInActivity(LoginFragment())
    }

    private fun setupRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://backend-budgetin.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    private fun showUpdateCurrentAmountDialog(goal: Goal) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_progress, null)
        val currentAmountInput = dialogView.findViewById<TextInputEditText>(R.id.currentAmountInput)

        AlertDialog.Builder(requireContext())
            .setTitle("Update Jumlah Saat Ini")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val newCurrentAmount = currentAmountInput.text.toString().toDoubleOrNull()
                if (newCurrentAmount != null) {
                    updateGoalCurrentAmount(goal.id, newCurrentAmount)
                } else {
                    Toast.makeText(context, "Jumlah tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateGoalCurrentAmount(goalId: String, currentAmount: Double) {
        val token = getToken() ?: return

        val goal = goalAdapter.getGoals().find { it.id == goalId }
        if (goal != null) {
            // Buat request dengan currentAmount langsung
            val updateRequest = GoalUpdateRequest(
                name = goal.goalName ?: "",
                targetAmount = goal.targetAmount,
                deadline = goal.duedate,
                currentAmount = currentAmount  // Kirim currentAmount langsung
            )

            val retrofit = setupRetrofit()
            val goalsApi = retrofit.create(GoalsApiService::class.java)

            // Debug logs
            Log.d("GoalsFragment", "Goal ID: $goalId")
            Log.d("GoalsFragment", "Current Amount: $currentAmount")
            Log.d("GoalsFragment", "Target Amount: ${goal.targetAmount}")
            Log.d("GoalsFragment", "Update Request: $updateRequest")

            goalsApi.updateGoal(goalId, "Bearer $token", updateRequest)
                .enqueue(object : Callback<GoalResponse> {
                    override fun onResponse(call: Call<GoalResponse>, response: Response<GoalResponse>) {
                        // Log response
                        Log.d("GoalsFragment", "Response Code: ${response.code()}")
                        Log.d("GoalsFragment", "Response Body: ${response.body()}")
                        if (!response.isSuccessful) {
                            Log.e("GoalsFragment", "Error Body: ${response.errorBody()?.string()}")
                        }

                        activity?.runOnUiThread {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Jumlah saat ini berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                loadGoals()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("GoalsFragment", "Update error: $errorBody")
                                Toast.makeText(context, "Gagal memperbarui jumlah saat ini: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<GoalResponse>, t: Throwable) {
                        Log.e("GoalsFragment", "Network Error: ${t.message}")
                        Log.e("GoalsFragment", "Stack trace:", t)
                        
                        activity?.runOnUiThread {
                            Toast.makeText(context, "Gagal terhubung ke server: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } else {
            Toast.makeText(context, "Goal tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog(goal: Goal) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Goal")
            .setMessage("Apakah Anda yakin ingin menghapus goal '${goal.goalName}'?")
            .setPositiveButton("Ya") { _, _ ->
                deleteGoal(goal.id)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
