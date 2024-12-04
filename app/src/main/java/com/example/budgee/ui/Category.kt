package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.budgee.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class Category : BottomSheetDialogFragment() {

    // Define an interface for category selection
    interface CategorySelectionListener {
        fun onCategorySelected(category: String)
    }

    private lateinit var listener: CategorySelectionListener
    private var showGajiBonus: Boolean = true // Flag to control visibility
    private var showFoodTravelingShopMedicalInvestmentTransportationAnother : Boolean = true

    // You should pass the listener to this fragment to communicate back to the parent
    fun setCategorySelectionListener(listener: CategorySelectionListener, showGajiBonus: Boolean = true, showFoodTravelingShopMedicalInvestmentTransportationAnother: Boolean=true) {
        this.listener = listener
        this.showGajiBonus = showGajiBonus
        this.showFoodTravelingShopMedicalInvestmentTransportationAnother = showFoodTravelingShopMedicalInvestmentTransportationAnother
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category, container, false)

        // Category options (ensure these are LinearLayouts, not TextViews)
        val categoryOption1: LinearLayout = view.findViewById(R.id.categoryOption1)
        val categoryOption2: LinearLayout = view.findViewById(R.id.categoryOption2)
        val categoryOption3: LinearLayout = view.findViewById(R.id.categoryOption3)
        val categoryOption4: LinearLayout = view.findViewById(R.id.categoryOption4)
        val categoryOption5: LinearLayout = view.findViewById(R.id.categoryOption5)
        val categoryOption6: LinearLayout = view.findViewById(R.id.categoryOption6)
        val categoryOption7: LinearLayout = view.findViewById(R.id.categoryOption7)
        val categoryOption8: LinearLayout = view.findViewById(R.id.categoryOption8)
        val categoryOption9: LinearLayout = view.findViewById(R.id.categoryOption9)

        // Display or hide "Gaji" and "Bonus" based on the flag
        if (!showGajiBonus) {
            categoryOption1.visibility = View.GONE  // Gaji
            categoryOption2.visibility = View.GONE // Bonus
        }
        if (!showFoodTravelingShopMedicalInvestmentTransportationAnother) {
            categoryOption3.visibility = View.GONE
            categoryOption4.visibility = View.GONE
            categoryOption5.visibility = View.GONE
            categoryOption6.visibility = View.GONE
            categoryOption7.visibility = View.GONE
            categoryOption8.visibility = View.GONE
            categoryOption9.visibility = View.GONE
        }
        // Set click listeners for category options
        categoryOption1.setOnClickListener {
            listener.onCategorySelected("Gaji") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        categoryOption2.setOnClickListener {
            listener.onCategorySelected("Bonus") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        categoryOption3.setOnClickListener {
            listener.onCategorySelected("Food") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        categoryOption4.setOnClickListener {
            listener.onCategorySelected("Traveling") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        categoryOption5.setOnClickListener {
            listener.onCategorySelected("Shop") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        categoryOption6.setOnClickListener {
            listener.onCategorySelected("Medical") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        categoryOption7.setOnClickListener {
            listener.onCategorySelected("Investment") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        categoryOption8.setOnClickListener {
            listener.onCategorySelected("Transportation") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        categoryOption9.setOnClickListener {
            listener.onCategorySelected("Another") // Send selected category
            dismiss() // Dismiss the bottom sheet
        }

        return view
    }
}
