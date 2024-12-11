package com.example.budgee.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgee.MainActivity
import com.example.budgee.R
import com.example.budgee.json.LoginRequest
import com.example.budgee.json.LoginResponse
import com.example.budgee.json.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.button.MaterialButton

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Menyembunyikan Bottom Navigation saat berada di halaman Login
        (activity as MainActivity).hideBottomNavigation()

        // Inisialisasi elemen UI
        val emailInput = view.findViewById<EditText>(R.id.email)
        val passwordInput = view.findViewById<EditText>(R.id.password)
        val loginButton = view.findViewById<MaterialButton>(R.id.btnLogin)
        val registerLink = view.findViewById<TextView>(R.id.register_link)

        // Navigasi ke RegisterFragment
        registerLink.setOnClickListener {
            (activity as MainActivity).replaceFragmentInActivity(RegisterFragment())
        }

        // Proses login ketika tombol login ditekan
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            } else {
                // Memanggil API untuk login
                loginUser(email, password)
            }
        }

        return view
    }

    private fun loginUser(email: String, password: String) {
        val authApiService = RetrofitInstance.authRetrofit.create(com.example.budgee.json.AuthApiService::class.java)
        val loginRequest = LoginRequest(email, password)

        authApiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginFragment", "Response: ${response.body()}")
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    Log.d("LoginFragment", "Login success: ${loginResponse?.success}, Message: ${loginResponse?.message}")
                    if (loginResponse?.success == true) {
                        // Simpan status login di SharedPreferences
                        val sharedPrefs = activity?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        sharedPrefs?.edit()?.putBoolean("is_logged_in", true)?.apply()

                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()

                        // Menggunakan Intent untuk berpindah ke MainActivity
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear stack
                        startActivity(intent)

                    } else {
                        // Tampilkan pesan yang sesuai jika login gagal
                        Toast.makeText(requireContext(), loginResponse?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
