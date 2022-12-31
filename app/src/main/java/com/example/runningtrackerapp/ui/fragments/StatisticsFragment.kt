package com.example.runningtrackerapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.runningtrackerapp.R
import com.example.runningtrackerapp.databinding.FragmentStatisticsBinding
import com.example.runningtrackerapp.ui.StatisticsViewModel
import com.example.runningtrackerapp.utils.CustomMarkerView
import com.example.runningtrackerapp.utils.Utils
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding> (
    FragmentStatisticsBinding::inflate
        ){

    private val viewModel : StatisticsViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            subscribeToObservers()
            setupBarChart()

    }


    private fun setupBarChart(){

        bind.barChart.xAxis.apply{
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        bind.barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        bind.barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        bind.barChart.apply {
            description.text = "Avg speed over time"
            legend.isEnabled = false
        }

    }

    private fun subscribeToObservers(){

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner){
            it?.let{
                bind.tvAverageSpeed.text = it.toString()}
        }

        viewModel.totalTime.observe(viewLifecycleOwner){
            it?.let {
                bind.tvTotalTime.text = Utils.timerFormat(it, false)
            }
        }

        viewModel.totalDistance.observe(viewLifecycleOwner){
            it?.let{
                bind.tvTotalDistance.text = (it / 1000f).toString()
            }
        }
        viewModel.totalCalories.observe(viewLifecycleOwner){
            it?.let{
                bind.tvTotalCalories.text = it.toString()

            }
        }

        viewModel.runsSortedByDate.observe(viewLifecycleOwner){

            it?.let{


                val allRunsSpeed = it.indices.map { i ->
                    BarEntry(i.toFloat(), it[i].avgSpeedInKm)
                }
                val barDataSet = BarDataSet(allRunsSpeed, "Average speed over time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), R.color.colorAccent)
                }
                    bind.barChart.data = BarData(barDataSet)
                    bind.barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.chart_pop_up)
                    bind.barChart.invalidate()
            }
        }



    }




}