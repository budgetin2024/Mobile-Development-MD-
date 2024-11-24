package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgee.R
import com.example.budgee.helper.UserDatabaseHelper

class LoginFragment : Fragment() {

    private lateinit var databaseHelper: UserDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Inisialisasi elemen UI
        val emailInput = view.findViewById<EditText>(R.id.email)
        val passwordInput = view.findViewById<EditText>(R.id.password)
        val loginButton = view.findViewById<ImageButton>(R.id.btn_sign_in)
        val forgotPassword = view.findViewById<TextView>(R.id.forgot_password)

        // Inisialisasi database helper
        databaseHelper = UserDatabaseHelper(requireContext())

        // Event tombol login
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val user = databaseHelper.getUserByEmail(email)
                if (user != null && user.moveToFirst()) {
                    val storedPassword = user.getString(user.getColumnIndexOrThrow(
                        UserDatabaseHelper.COLUMN_PASSWORD))
                    if (password == storedPassword) {
                        Toast.makeText(requireContext(), "Login successful!", Toast.LENGTH_SHORT).show()
                        // Tambahkan navigasi ke halaman berikutnya jika diperlukan
                    } else {
                        Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
                user?.close()
            }
        }

        // Event untuk "Forgot Password"
        forgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Forgot password clicked!", Toast.LENGTH_SHORT).show()
            // Tambahkan logika untuk menangani lupa password
        }

        return view
    }
}
