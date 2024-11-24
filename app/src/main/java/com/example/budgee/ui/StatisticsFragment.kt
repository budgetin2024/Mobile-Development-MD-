package com.example.budgee.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgee.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class StatisticsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find LineChart
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)

        // Sample Data Entries
        val incomeEntries = listOf(
            Entry(0f, 300f),
            Entry(1f, 400f),
            Entry(2f, 500f),
            Entry(3f, 450f)
        )

        val outcomeEntries = listOf(
            Entry(0f, 200f),
            Entry(1f, 350f),
            Entry(2f, 400f),
            Entry(3f, 300f)
        )

        val savingsEntries = listOf(
            Entry(0f, 100f),
            Entry(1f, 150f),
            Entry(2f, 200f),
            Entry(3f, 150f)
        )

        // Line DataSets
        val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
            color = Color.BLUE
            valueTextColor = Color.BLUE
            lineWidth = 2f
        }

        val outcomeDataSet = LineDataSet(outcomeEntries, "Outcome").apply {
            color = Color.RED
            valueTextColor = Color.RED
            lineWidth = 2f
        }

        val savingsDataSet = LineDataSet(savingsEntries, "Savings").apply {
            color = Color.YELLOW
            valueTextColor = Color.YELLOW
            lineWidth = 2f
        }

        // Combine DataSets into LineData
        val lineData = LineData(incomeDataSet, outcomeDataSet, savingsDataSet)

        // Set Data to Chart
        lineChart.data = lineData

        // Customize Chart Appearance
        lineChart.description.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.isDragEnabled = true
        lineChart.setScaleEnabled(true)
        lineChart.setPinchZoom(true)

        // Customize Legend
        val legend = lineChart.legend
        legend.form = Legend.LegendForm.LINE
        legend.textSize = 12f

        // Animate the Chart
        lineChart.animateX(1500)
    }
}
