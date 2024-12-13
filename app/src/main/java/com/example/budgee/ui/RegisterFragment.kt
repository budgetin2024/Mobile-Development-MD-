package com.example.budgee.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.budgee.json.RegisterRequest
import com.example.budgee.json.RegisterResponse
import com.example.budgee.json.RetrofitInstance
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Menyembunyikan Bottom Navigation saat berada di halaman Register
        (activity as MainActivity).hideBottomNavigation()

        // Inisialisasi elemen UI
        val nameInput = view.findViewById<EditText>(R.id.name)
        val emailInput = view.findViewById<EditText>(R.id.email)
        val passwordInput = view.findViewById<EditText>(R.id.password)
        val signUpButton = view.findViewById<MaterialButton>(R.id.btn_sign_up)
        val loginLink = view.findViewById<TextView>(R.id.login_link)

        loginLink.setOnClickListener {
            (activity as MainActivity).replaceFragmentInActivity(LoginFragment())
        }

        signUpButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            Log.d("EmailInput", "Email yang diinput: $email")
            val password = passwordInput.text.toString().trim()

            // Validasi input
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Memanggil API untuk registrasi
                registerUser(name, email, password)
            }
        }

        return view
    }

    private fun registerUser(name: String, email: String, password: String) {
        val authApiService = RetrofitInstance.authRetrofit.create(com.example.budgee.json.AuthApiService::class.java)
        val registerRequest = RegisterRequest(name, email, password)

        Log.d("RegisterFragment", "Memulai proses registrasi untuk email: $email")

        authApiService.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                Log.d("RegisterFragment", "Response received: ${response.code()}")
                Log.d("RegisterFragment", "Response body: ${response.body()}")
                
                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse?.message?.contains("successfully", ignoreCase = true) == true) {
                            Log.d("RegisterFragment", "Registrasi berhasil, mencoba navigasi ke LoginFragment")
                            
                            // Tampilkan pesan sukses
                            Toast.makeText(requireContext(), "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                            
                            try {
                                // Langsung navigasi ke LoginFragment
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.fragment_container, LoginFragment())
                                    .commit()
                            } catch (e: Exception) {
                                Log.e("RegisterFragment", "Error during navigation: ${e.message}")
                            }
                        } else {
                            val errorMessage = registerResponse?.error ?: registerResponse?.message ?: "Registrasi gagal"
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                            Log.e("RegisterFragment", "Registration failed: $errorMessage")
                        }
                    } else {
                        Toast.makeText(requireContext(), "Registrasi gagal", Toast.LENGTH_SHORT).show()
                        Log.e("RegisterFragment", "Registration failed with code: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("RegisterFragment", "Network error: ${t.message}")
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}
