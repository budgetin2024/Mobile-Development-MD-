package com.example.budgee

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgee.databinding.ActivityMainBinding
import com.example.budgee.welcome.OnboardingFragment
import com.example.budgee.ui.HomeFragment
import com.example.budgee.ui.ProfileFragment
import com.example.budgee.ui.StatisticsFragment
import com.example.budgee.welcome.OnboardingFragment2
import com.example.budgee.welcome.OnboardingFragment3

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Terapkan padding hanya pada fragment container, bukan pada keseluruhan layout
            binding.fragmentContainer.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // Initialize fragments and onboarding
        if (savedInstanceState == null) {
            showInitialFragment()
        }

        // Setup BottomNavigationView
        setupBottomNavigation()
    }

    private fun showInitialFragment() {
        val onboardingShown = checkIfOnboardingShown() // Simpan status onboarding di SharedPreferences
        if (!onboardingShown) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OnboardingFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    binding.bottomNavigation.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.blue)
                    )
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_stats -> {
                    binding.bottomNavigation.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.blue)
                    )
                    replaceFragment(StatisticsFragment())
                    true
                }
                R.id.nav_wallet -> {
                    binding.bottomNavigation.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.blue)
                    )
                    replaceFragment(OnboardingFragment3())
                    true
                }
                R.id.nav_profile -> {
                    binding.bottomNavigation.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.blue)
                    )
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }


    private fun replaceFragment(fragment: androidx.fragment.app.Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        return true
    }

    private fun checkIfOnboardingShown(): Boolean {
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPrefs.getBoolean("onboarding_shown", false)
    }
}
