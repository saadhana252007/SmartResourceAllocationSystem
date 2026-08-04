package com.example.smartresourceallocation.ui.admin.fragments.analytics

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.smartresourceallocation.R
import com.example.smartresourceallocation.databinding.AdminFragmentAnalyticsBinding
import com.example.smartresourceallocation.utils.SharedPrefManager
import com.example.smartresourceallocation.viewmodel.AnalyticsViewModel
import android.graphics.Color
import android.widget.TextView
import com.example.smartresourceallocation.model.AnalyticsResponse
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.smartresourceallocation.utils.DateUtils


class AnalyticsFragment :
    Fragment(R.layout.admin_fragment_analytics) {

    private var _binding:
            AdminFragmentAnalyticsBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var viewModel:
            AnalyticsViewModel

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        _binding =
            AdminFragmentAnalyticsBinding.bind(view)

        viewModel =
            ViewModelProvider(this)[
                AnalyticsViewModel::class.java
            ]

        val token = SharedPrefManager(requireContext()).getToken()

        if (token == null) {
            Toast.makeText(
                requireContext(),
                "Please login again",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.getAnalytics("Bearer $token")

        observeData()

    }

    private fun observeData() {

        viewModel.analytics.observe(
            viewLifecycleOwner
        ) { analytics ->

            binding.tvTotalResources.text =
                analytics.totalResources.toString()

            binding.tvTotalReservations.text =
                analytics.totalReservations.toString()

            binding.tvPending.text =
                analytics.pending.toString()

            binding.tvApproved.text =
                analytics.approved.toString()

            binding.tvWaitlisted.text =
                analytics.waitlisted.toString()

            binding.tvRejected.text =
                analytics.rejected.toString()

            binding.tvCancelled.text =
                analytics.cancelled.toString()

            setupPieChart(analytics)

            setupBarChart(analytics)

            setupTopResourcesChart(analytics)

            setupReservationTrendChart(analytics)

            setupInsights(analytics)

            setupUtilization(analytics)

        }

        viewModel.errorMessage.observe(
            viewLifecycleOwner
        ) {

            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    override fun onDestroyView() {

        super.onDestroyView()

        _binding = null

    }
    private fun setupPieChart(
        analytics: AnalyticsResponse
    ) {

        val entries = arrayListOf<PieEntry>()

        entries.add(
            PieEntry(
                analytics.pending.toFloat(),
                "Pending"
            )
        )

        entries.add(
            PieEntry(
                (analytics.approved +
                        analytics.alternativeApproved).toFloat(),
                "Approved"
            )
        )

        entries.add(
            PieEntry(
                analytics.waitlisted.toFloat(),
                "Waitlisted"
            )
        )

        entries.add(
            PieEntry(
                analytics.rejected.toFloat(),
                "Rejected"
            )
        )

        entries.add(
            PieEntry(
                analytics.cancelled.toFloat(),
                "Cancelled"
            )
        )

        val dataSet =
            PieDataSet(entries,"")

        dataSet.colors = listOf(

            Color.parseColor("#FFC107"),

            Color.parseColor("#4CAF50"),

            Color.parseColor("#9C27B0"),

            Color.parseColor("#F44336"),

            Color.GRAY

        )

        dataSet.valueTextSize = 14f

        dataSet.valueTextColor = Color.WHITE

        val pieData =
            PieData(dataSet)

        binding.pieChart.description.isEnabled =
            false

        binding.pieChart.centerText =
            "Reservations"

        binding.pieChart.setCenterTextSize(18f)

        binding.pieChart.animateY(1200)
        binding.pieChart.clear()

        binding.pieChart.data = pieData

        binding.pieChart.notifyDataSetChanged()

        binding.pieChart.invalidate()
        binding.pieChart.setDrawEntryLabels(false)


    }
    private fun setupBarChart(

        analytics: AnalyticsResponse

    ){

        val entries = ArrayList<BarEntry>()

        val labels = ArrayList<String>()

        analytics.categoryUsage.forEachIndexed { index, category ->

            entries.add(

                BarEntry(

                    index.toFloat(),

                    category.count.toFloat()

                )

            )

            labels.add(
                when(category._id){

                    "Meeting Room" -> "Meeting"

                    "Sports Facility" -> "Sports"

                    "Laboratory Equipment" -> "Lab"

                    "Study Area" -> "Study"

                    else -> category._id
                }
            )

        }

        val dataSet =

            BarDataSet(

                entries,

                ""

            )
        binding.barChart.legend.isEnabled = false

        dataSet.color =

            Color.parseColor("#1B3A57")

        dataSet.valueTextSize = 12f

        val data =

            BarData(dataSet)

        binding.barChart.description.isEnabled = false

        binding.barChart.animateY(1000)

        binding.barChart.xAxis.valueFormatter =

            IndexAxisValueFormatter(labels)

        binding.barChart.xAxis.position =

            XAxis.XAxisPosition.BOTTOM

        binding.barChart.xAxis.granularity = 1f

        binding.barChart.xAxis.setDrawGridLines(false)

        binding.barChart.axisRight.isEnabled = false
        binding.barChart.clear()

        binding.barChart.data = data

        binding.barChart.notifyDataSetChanged()

        binding.barChart.invalidate()
        binding.barChart.axisLeft.granularity = 1f
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.legend.isEnabled = false

        binding.barChart.description.isEnabled = false

        binding.barChart.animateY(1000)

        binding.barChart.axisRight.isEnabled = false

        binding.barChart.xAxis.position =
            XAxis.XAxisPosition.BOTTOM

        binding.barChart.xAxis.granularity = 1f

        binding.barChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(labels)

        binding.barChart.xAxis.setDrawGridLines(false)

        binding.barChart.axisLeft.axisMinimum = 0f

        binding.barChart.invalidate()


    }
    private fun setupTopResourcesChart(

        analytics: AnalyticsResponse

    ) {

        val entries = ArrayList<BarEntry>()

        val labels = ArrayList<String>()

        analytics.topResources.take(5).forEachIndexed { index, resource ->

            entries.add(
                BarEntry(
                    index.toFloat(),
                    resource.reservations.toFloat()
                )
            )

            labels.add(resource._id.name)

        }

        val dataSet = BarDataSet(
            entries,
            ""
        )

        dataSet.color =
            Color.parseColor("#1B3A57")

        dataSet.valueTextSize = 13f

        dataSet.valueFormatter =
            object : ValueFormatter() {

                override fun getBarLabel(
                    barEntry: BarEntry?
                ): String {

                    return barEntry?.y?.toInt().toString()

                }

            }

        val data = BarData(dataSet)

        data.barWidth = 0.55f

        binding.horizontalBarChart.clear()

        binding.horizontalBarChart.data = data

        binding.horizontalBarChart.description.isEnabled = false

        binding.horizontalBarChart.legend.isEnabled = false

        binding.horizontalBarChart.animateY(1000)

        binding.horizontalBarChart.setFitBars(true)

        binding.horizontalBarChart.axisRight.isEnabled = false

        binding.horizontalBarChart.axisLeft.axisMinimum = 0f

        binding.horizontalBarChart.axisLeft.granularity = 1f

        binding.horizontalBarChart.axisLeft.valueFormatter =
            object : ValueFormatter() {

                override fun getFormattedValue(
                    value: Float
                ): String {

                    return value.toInt().toString()

                }

            }

        binding.horizontalBarChart.xAxis.position =
            XAxis.XAxisPosition.BOTTOM

        binding.horizontalBarChart.xAxis.granularity = 1f

        binding.horizontalBarChart.xAxis.labelCount =
            labels.size

        binding.horizontalBarChart.xAxis.textSize = 11f

        binding.horizontalBarChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(labels)

        binding.horizontalBarChart.xAxis.setDrawGridLines(false)

        binding.horizontalBarChart.setExtraOffsets(
            50f,
            20f,
            20f,
            20f
        )

        binding.horizontalBarChart.invalidate()

    }

    private fun setupReservationTrendChart(

        analytics: AnalyticsResponse

    ) {

        val entries = ArrayList<Entry>()

        val labels = ArrayList<String>()

        analytics.reservationTrend.forEachIndexed { index, trend ->

            entries.add(

                Entry(

                    index.toFloat(),

                    trend.reservations.toFloat()

                )

            )

            labels.add(
                DateUtils.formatShortDate(
                    trend._id
                )
            )

        }

        val dataSet = LineDataSet(

            entries,

            ""

        )

        dataSet.color =
            Color.parseColor("#1B3A57")

        dataSet.setCircleColor(
            Color.parseColor("#1B3A57")
        )

        dataSet.circleRadius = 5f

        dataSet.lineWidth = 3f

        dataSet.valueTextSize = 11f

        dataSet.setDrawFilled(true)

        dataSet.fillColor =
            Color.parseColor("#B3D9F2")

        dataSet.valueFormatter =
            object : ValueFormatter() {

                override fun getPointLabel(
                    entry: Entry?
                ): String {

                    return entry?.y?.toInt().toString()

                }

            }

        val data =
            LineData(dataSet)

        binding.lineChart.clear()

        binding.lineChart.data = data

        binding.lineChart.description.isEnabled = false

        binding.lineChart.legend.isEnabled = false

        binding.lineChart.axisRight.isEnabled = false

        binding.lineChart.axisLeft.axisMinimum = 0f

        binding.lineChart.axisLeft.granularity = 1f

        binding.lineChart.xAxis.position =
            XAxis.XAxisPosition.BOTTOM

        binding.lineChart.xAxis.granularity = 1f

        binding.lineChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(labels)

        binding.lineChart.xAxis.setDrawGridLines(false)

        binding.lineChart.animateX(1200)

        binding.lineChart.invalidate()

    }

    private fun setupInsights(

        analytics: AnalyticsResponse

    ){

        binding.tvMostReserved.text =

            " Most Reserved Resource : ${
                analytics.insights.mostReservedResource
            }"

        binding.tvHighestUtilization.text =
            if (analytics.insights.highestUtilization != null) {

                " Highest Utilization : ${
                    analytics.insights.highestUtilization._id.name
                } (${analytics.insights.highestUtilization.reservations})"

            } else {

                " Highest Utilization : N/A"

            }

        binding.tvLeastUtilization.text =
            if (analytics.insights.leastUtilization != null) {

                " Least Utilized : ${
                    analytics.insights.leastUtilization._id.name
                } (${analytics.insights.leastUtilization.reservations})"

            } else {

                " Least Utilized : N/A"

            }




        binding.tvPeakDay.text =

            " Peak Reservation Day : ${
                analytics.insights.peakReservationDay?.let {

                    DateUtils.formatShortDate(it)

                } ?: "N/A"
            }"

        binding.tvAverageDuration.text =

            " Average Duration : ${
                String.format(
                    "%.1f",
                    analytics.insights.averageReservationDuration
                )
            } Hours"

    }
    private fun setupUtilization(

        analytics: AnalyticsResponse

    ) {

        binding.layoutUtilization.removeAllViews()

        analytics.utilization.forEach {

            val tv = TextView(requireContext())

            tv.text =
                "📊 ${it._id.name} : ${it.reservations} Reservations"

            tv.textSize = 16f

            tv.setPadding(0,16,0,16)

            tv.setTextColor(
                Color.parseColor("#1B3A57")
            )

            binding.layoutUtilization.addView(tv)

        }

    }

}