package com.example.budgee.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.budgee.R
import com.example.budgee.MainActivity

class ProfileFragment : Fragment() {

    private lateinit var signOutButton: Button
    private lateinit var editProfileButton: Button
    private lateinit var sharedPreferences: SharedPreferences

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
        // Menangani klik tombol Sign Out
        signOutButton.setOnClickListener {
            logout()
        }
        editProfileButton.setOnClickListener{
            editProfile()
        }

        return view
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
