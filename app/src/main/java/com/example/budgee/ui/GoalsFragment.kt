    package com.example.budgee.ui

    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.Button
    import android.widget.ImageView
    import androidx.fragment.app.Fragment
    import com.example.budgee.R
    import com.example.budgee.MainActivity

    class GoalsFragment : Fragment() {

        private lateinit var createGoalButton: Button

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_goals, container, false)

            // Hide bottom navigation
            (activity as? MainActivity)?.hideBottomNavigation()

            // Initialize the button
            createGoalButton = view.findViewById(R.id.createGoalButton)

            // Set the click listener for the "Create Goal" button
            createGoalButton.setOnClickListener {
                navigateToCreateGoal()
            }

            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            // Inisialisasi back icon
            val backIcon = view.findViewById<ImageView>(R.id.backIcon)
            
            // Set click listener untuk back icon
            backIcon.setOnClickListener {
                // Kembali ke HomeFragment
                (activity as? MainActivity)?.apply {
                    replaceFragmentInActivity(HomeFragment(), false) // false agar tidak ditambahkan ke back stack
                    showBottomNavigation() // Pastikan bottom navigation muncul
                }
            }
        }

        private fun navigateToCreateGoal() {
            // Aksi saat tombol "Create Goal" diklik, berpindah ke CreateGoalsFragment
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, CreateGoalsFragment()) // Ganti dengan fragment container ID Anda
            transaction.addToBackStack(null)
            transaction.commit()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            // Show bottom navigation when this fragment is destroyed
            (activity as? MainActivity)?.showBottomNavigation()
        }
    }
