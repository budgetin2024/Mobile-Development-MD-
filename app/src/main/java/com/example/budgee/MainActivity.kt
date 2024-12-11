package com.example.budgee

import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.budgee.databinding.ActivityMainBinding
import com.example.budgee.ui.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.fragmentContainer.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Initial fragment load based on login status
        if (savedInstanceState == null) {
            showInitialFragment()
        }

        // Setup bottom navigation
        setupBottomNavigation()
    }

    // Check user login status and show the appropriate fragment
    private fun showInitialFragment() {
        val isLoggedIn = checkIfUserLoggedIn()

        if (isLoggedIn) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment()) // Show HomeFragment if logged in
                .commit()
            showBottomNavigation()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment()) // Show LoginFragment if not logged in
                .commit()
            hideBottomNavigation()
        }
    }

    // Setup bottom navigation menu and its actions
    private fun setupBottomNavigation() {
        binding.bottomNavigation.background = ContextCompat.getDrawable(this, R.drawable.custom_bottom_nav_background)

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            val menuItemView = binding.bottomNavigation.findViewById<View>(menuItem.itemId)

            // Animation for menu item icon (up and down)
            val translateAnimator = ObjectAnimator.ofFloat(menuItemView, "translationY", 0f, -20f, 0f)
            translateAnimator.duration = 300
            translateAnimator.start()

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    replaceFragmentInActivity(HomeFragment())
                    showBottomNavigation()
                    true
                }
                R.id.nav_stats -> {
                    replaceFragmentInActivity(StatisticsFragment())
                    showBottomNavigation()
                    true
                }
                R.id.nav_add -> {
                    showCustomPopup()
                    false
                }
                R.id.nav_news -> {
                    replaceFragmentInActivity(NewsFragment())
                    showBottomNavigation()
                    true
                }
                R.id.nav_profile -> {
                    replaceFragmentInActivity(ProfileFragment())
                    showBottomNavigation()
                    true
                }
                else -> false
            }
        }
    }

    // Function to replace fragment that can be accessed from other fragments
    fun replaceFragmentInActivity(fragment: Fragment, addToBackStack: Boolean = true) {
        Log.d("MainActivity", "Replacing fragment with: ${fragment.javaClass.simpleName}")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    // Show the bottom navigation
    fun showBottomNavigation() {
        binding.bottomNavigation.visibility = View.VISIBLE
    }

    // Hide the bottom navigation
    fun hideBottomNavigation() {
        binding.bottomNavigation.visibility = View.GONE
    }

    // Handle custom popup for "Add" action
    private fun showCustomPopup() {
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.popup_menu, null)

        dialog.setContentView(view)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            val params = attributes
            params.y = -200 // Position the dialog above Bottom Navigation
            attributes = params
        }

        // Handle the popup button clicks
        val btnIncome = view.findViewById<ImageView>(R.id.btn_income)
        val btnTransfer = view.findViewById<ImageView>(R.id.btn_transfer)
        val btnExpense = view.findViewById<ImageView>(R.id.btn_expense)

        btnIncome.setOnClickListener {
            replaceFragmentInActivity(IncomeFragment())
            dialog.dismiss()
        }
        btnTransfer.setOnClickListener {
            replaceFragmentInActivity(GoalsFragment())
            dialog.dismiss()
        }
        btnExpense.setOnClickListener {
            replaceFragmentInActivity(OutcomeFragment())
            dialog.dismiss()
        }

        dialog.show()
    }

    // Check if the user is logged in (stored in SharedPreferences)
    private fun checkIfUserLoggedIn(): Boolean {
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPrefs.getBoolean("is_logged_in", false) // Assuming 'is_logged_in' is stored here
    }
}
