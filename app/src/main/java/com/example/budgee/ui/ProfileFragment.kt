package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.budgee.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        // Setup click listener for Edit Profile button
        val editProfileButton: View = rootView.findViewById(R.id.pengaturan)
        editProfileButton.setOnClickListener {
            // Navigate to EditProfileFragment manually
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, EditProfileFragment()) // Replace the current fragment
            transaction.addToBackStack(null) // Add this transaction to the back stack
            transaction.commit()
        }

        return rootView
    }
}
