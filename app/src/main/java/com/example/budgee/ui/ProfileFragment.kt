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
import com.example.budgee.R
import com.example.budgee.MainActivity
import com.example.budgee.json.UserData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

class ProfileFragment : Fragment() {

    private lateinit var signOutButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userNameTextView: TextView  // Add a TextView to show user name
    private lateinit var userEmailTextView: TextView // Add a TextView to show user email

    // Define Retrofit and ApiService directly in the fragment
    private val retrofit by lazy {
        // Logging interceptor for debugging network requests
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://backend-budgetin.et.r.appspot.com/")  // Your API base URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    interface ApiService {
        @GET("auth/user")
        fun getUserData(@Header("Authorization") token: String): Call<UserData>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Menginisialisasi SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        // Menemukan tombol Sign Out
        signOutButton = view.findViewById(R.id.signOutButton)
        editProfileButton = view.findViewById(R.id.pengaturan)
        userNameTextView = view.findViewById(R.id.username) // TextView for user name
        userEmailTextView = view.findViewById(R.id.email) // TextView for user email

        // Menangani klik tombol Sign Out
        signOutButton.setOnClickListener {
            logout()
        }

        // Menangani klik tombol Edit Profile
        editProfileButton.setOnClickListener {
            editProfile()
        }

        // Fetch user data
        fetchUserData()

        return view
    }

    private fun fetchUserData() {
        // Retrieve the token from SharedPreferences
        val token = sharedPreferences.getString("auth_token", null)

        if (token != null) {
            // Add the token to the request header for authentication
            val call = apiService.getUserData("Bearer $token") // Pass the token as a "Bearer" token
            call.enqueue(object : Callback<UserData> {
                override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                    if (response.isSuccessful) {
                        val userData = response.body()
                        userData?.let {
                            // Display user data in TextViews
                            userNameTextView.text = it.name
                            userEmailTextView.text = it.email
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserData>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Token not found", Toast.LENGTH_SHORT).show()
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
