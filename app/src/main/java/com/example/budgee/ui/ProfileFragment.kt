package com.example.budgee.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.budgee.R
import com.example.budgee.MainActivity
import com.example.budgee.json.ApiService
import com.example.budgee.json.UserData
import com.example.budgee.model.ProfileViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor


class ProfileFragment : Fragment() {

    private lateinit var signOutButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Inisialisasi views
        initializeViews(view)
        
        // Observe user data
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            userNameTextView.text = user.name
            userEmailTextView.text = user.email
        }

        // Initialize data from SharedPreferences
        viewModel.initializeFromSharedPreferences(requireContext())
        
        // Fetch fresh data from API
        fetchUserData()

        return view
    }

    private fun initializeViews(view: View) {
        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        
        // Inisialisasi views sesuai dengan ID di layout
        signOutButton = view.findViewById(R.id.signOutButton)
        userNameTextView = view.findViewById(R.id.username)
        userEmailTextView = view.findViewById(R.id.email)

        // Setup click listener
        signOutButton.setOnClickListener { logout() }
    }

    private fun fetchUserData() {
        val token = sharedPreferences.getString("auth_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(context, "Token tidak ditemukan. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            return
        }

        // Tambahkan logging untuk debug
        android.util.Log.d("ProfileFragment", "Token: $token")

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $token") // Pastikan format token benar
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

        apiService.getUserData("Bearer $token").enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                android.util.Log.d("ProfileFragment", "Response code: ${response.code()}")
                android.util.Log.d("ProfileFragment", "Response headers: ${response.headers()}")
                
                if (response.isSuccessful) {
                    val userData = response.body()
                    android.util.Log.d("ProfileFragment", "User data received: $userData")
                    
                    userData?.user?.let { user ->
                        activity?.runOnUiThread {
                            viewModel.setUserData(user)
                            
                            // Save to SharedPreferences
                            sharedPreferences.edit().apply {
                                putString("user_name", user.name)
                                putString("user_email", user.email)
                                apply()
                            }
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("ProfileFragment", "Error response: $errorBody")
                    
                    if (response.code() == 401 || response.code() == 400) {
                        activity?.runOnUiThread {
                            Toast.makeText(context, "Sesi telah berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
                            sharedPreferences.edit().remove("auth_token").apply()
                            (activity as? MainActivity)?.replaceFragmentInActivity(LoginFragment())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun handleErrorResponse(response: Response<UserData>) {
        val errorBody = response.errorBody()?.string()
        android.util.Log.e("ProfileFragment", "Error response: $errorBody")
        
        if (response.code() == 401) {
            activity?.runOnUiThread {
                Toast.makeText(context, "Sesi telah berakhir. Silakan login kembali.", Toast.LENGTH_SHORT).show()
                sharedPreferences.edit().remove("auth_token").apply()
                (activity as? MainActivity)?.replaceFragmentInActivity(LoginFragment())
            }
        }
    }

    private fun logout() {
        // Menghapus token atau data yang tersimpan
        val editor = sharedPreferences.edit()
        editor.clear() // Menghapus semua data pengguna dari SharedPreferences
        editor.apply()

        // Tampilkan pesan sukses
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

        // Arahkan pengguna ke halaman Login
        (requireActivity() as MainActivity).replaceFragmentInActivity(LoginFragment(), addToBackStack = false)

        // Menghentikan aktivitas atau mengakhiri sesi ini (opsional jika perlu)
        requireActivity().finish()
    }

    private fun editProfile() {
        (requireActivity() as MainActivity).replaceFragmentInActivity(EditProfileFragment(), addToBackStack = true)
    }
}
