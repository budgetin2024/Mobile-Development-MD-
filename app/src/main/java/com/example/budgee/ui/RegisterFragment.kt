package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgee.R
import com.example.budgee.helper.UserDatabaseHelper

class RegisterFragment : Fragment() {

    private lateinit var databaseHelper: UserDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Inisialisasi elemen UI
        val emailInput = view.findViewById<EditText>(R.id.email)
        val passwordInput = view.findViewById<EditText>(R.id.password)
        val signUpButton = view.findViewById<ImageButton>(R.id.btn_sign_up)

        // Inisialisasi database helper
        databaseHelper = UserDatabaseHelper(requireContext())

        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val userId = databaseHelper.addUser(email, password)
                if (userId > 0) {
                    Toast.makeText(requireContext(), "User registered successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Error registering user", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }
}
