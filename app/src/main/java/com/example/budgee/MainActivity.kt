package com.example.budgee

import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.budgee.databinding.ActivityMainBinding
import com.example.budgee.ui.HomeFragment
import com.example.budgee.ui.NewsFragment
import com.example.budgee.ui.ProfileFragment
import com.example.budgee.ui.StatisticsFragment
import com.example.budgee.welcome.OnboardingFragment

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

        if (savedInstanceState == null) {
            showInitialFragment()
        }

        setupBottomNavigation()
    }

    private fun showInitialFragment() {
        val onboardingShown = checkIfOnboardingShown()
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
        binding.bottomNavigation.background = ContextCompat.getDrawable(this, R.drawable.custom_bottom_nav_background)

        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            val menuItemView = binding.bottomNavigation.findViewById<View>(menuItem.itemId)

            // Animasi untuk ikon (naik dan turun)
            val translateAnimator = ObjectAnimator.ofFloat(menuItemView, "translationY", 0f, -20f, 0f)
            translateAnimator.duration = 300
            translateAnimator.start()

            when (menuItem.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_stats -> {
                    replaceFragment(StatisticsFragment())
                    true
                }
                R.id.nav_add -> {
                    showCustomPopup()
                    false
                }
                R.id.nav_news -> {
                    replaceFragment(NewsFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }
    private fun showCustomPopup() {
        val dialog = Dialog(this)
        val view = layoutInflater.inflate(R.layout.popup_menu, null)

        // Set custom dialog layout
        dialog.setContentView(view)

        // Set dialog properties to appear above the Bottom Navigation
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            val params = attributes
            params.y = -200 // Adjust this value to position the dialog above Bottom Navigation
            attributes = params
        }

        // Handle button clicks
        val btnIncome = view.findViewById<ImageView>(R.id.btn_income)
        val btnTransfer = view.findViewById<ImageView>(R.id.btn_transfer)
        val btnExpense = view.findViewById<ImageView>(R.id.btn_expense)

        btnIncome.setOnClickListener {
            Toast.makeText(this, "Penerimaan clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        btnTransfer.setOnClickListener {
            Toast.makeText(this, "Transfer clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        btnExpense.setOnClickListener {
            Toast.makeText(this, "Pengeluaran clicked", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
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
