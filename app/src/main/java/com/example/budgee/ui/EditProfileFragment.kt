package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.budgee.R

class EditProfileFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var dobEditText: EditText
    private lateinit var saveChangesButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Initialize views
        usernameEditText = rootView.findViewById(R.id.username)
        firstNameEditText = rootView.findViewById(R.id.firstName)
        lastNameEditText = rootView.findViewById(R.id.lastName)
        dobEditText = rootView.findViewById(R.id.dob)
        saveChangesButton = rootView.findViewById(R.id.saveChangesButton)

        // Handle Save Changes button click
        saveChangesButton.setOnClickListener {
            // Retrieve user inputs
            val username = usernameEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val dob = dobEditText.text.toString()

            // Simulate saving changes (you can replace this with actual logic)
            // Example: Call an API or update the local database

            // Optionally show a message to the user
            // Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT).show()
        }

        return rootView
    }
}
