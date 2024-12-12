package com.example.budgee.ui

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.budgee.MainActivity
import com.example.budgee.R
import com.example.budgee.json.TransactionRequest
import com.example.budgee.json.TransactionResponse
import com.example.budgee.json.TransactionsApi
import com.example.budgee.model.HomeViewModel
import com.google.android.material.button.MaterialButton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class OutcomeFragment : Fragment(), Category.CategorySelectionListener {

    private lateinit var amountEditText: EditText
    private lateinit var dateValueTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var backIcon: ImageView
    private lateinit var remarksEditText: EditText

    private lateinit var transactionsApi: TransactionsApi
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outcome, container, false)

        // Hide Bottom Navigation when OutcomeFragment is active
        (activity as? MainActivity)?.hideBottomNavigation()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve token from SharedPreferences
        val sharedPrefs = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPrefs?.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "Token not found. Please login again.", Toast.LENGTH_SHORT).show()
            replaceFragment(LoginFragment())
            return
        }

        // Set up Retrofit with Authorization Token
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

        // Initialize Views
        amountEditText = view.findViewById(R.id.amountEditText)
        dateValueTextView = view.findViewById(R.id.dateValueTextView)
        categoryTextView = view.findViewById(R.id.categoryTextView)
        remarksEditText = view.findViewById(R.id.remarksEditText)
        backIcon = view.findViewById(R.id.backIcon)

        // Handle back icon click
        backIcon.setOnClickListener {
            replaceFragment(HomeFragment())
        }

        // Set current date as default
        val calendar = Calendar.getInstance()
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        dateValueTextView.text = currentDate

        // Handle date click to show DatePickerDialog
        dateValueTextView.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
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
            categoryBottomSheet.setCategorySelectionListener(this, showGajiBonus = false)
            categoryBottomSheet.show(parentFragmentManager, categoryBottomSheet.tag)
        }

        // Handle save button click
        view.findViewById<MaterialButton>(R.id.btnSave).setOnClickListener {
            val amountText = amountEditText.text.toString().trim()
            val category = categoryTextView.text.toString().trim()
            val date = dateValueTextView.text.toString().trim()
            val description = remarksEditText.text.toString().trim()

            if (amountText.isEmpty() || category.isEmpty() || date.isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val transaction = TransactionRequest(
                    amount = amountText.toDouble(),
                    category = category,
                    date = date,
                    type = "expense",
                    description = description
                )
                saveTransaction(transaction)
            }
        }
    }


    private fun saveTransaction(transaction: TransactionRequest) {
        transactionsApi.addTransaction(transaction).enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(context, "Outcome saved successfully", Toast.LENGTH_SHORT).show()
                    response.body()?.let { transactionResponse ->
                        viewModel.addNewTransaction(transactionResponse)
                    }
                    (requireActivity() as MainActivity).replaceFragmentInActivity(HomeFragment())
                } else {
                    Toast.makeText(context, "Failed to save outcome", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun replaceFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCategorySelected(category: String) {
        categoryTextView.text = category
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.showBottomNavigation()
    }
}
