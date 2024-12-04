package com.example.budgee.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.budgee.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class CategoryBottomSheetFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_category_bottom_sheet, container, false)

        // Set custom slide-up animation (if desired)
        val bottomSheetDialog = dialog as? BottomSheetDialog
        bottomSheetDialog?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED

        return view
    }


    // Handle item click here (for category selection)
    private fun handleCategoryClick(category: String) {
        // Do something with the selected category
        // For example, update the category TextView in the IncomeFragment
        dismiss()
    }
}
