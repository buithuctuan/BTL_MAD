package com.example.btl_mad.ui.statistics

import CustomMarkerView
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.btl_mad.R
import com.example.btl_mad.data.statistics.StatisticLineEntry
import com.example.btl_mad.data.statistics.StatisticPieEntry
import com.example.btl_mad.data.statistics.StatisticRepository
import com.example.btl_mad.data.statistics.StatisticTotalEntry
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.utils.SharedPrefManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.flexbox.FlexboxLayout
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class StatisticsFragment : BaseFragment() {

    private lateinit var pieChart: PieChart
    private lateinit var lineChart: LineChart
    private lateinit var llSpendingCard: CardView
    private lateinit var llIncomeCard: CardView
    private lateinit var tvTotalSpending: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvSpendChange: TextView
    private lateinit var tvIncomeChange: TextView
    private lateinit var tvFilter: TextView
    private lateinit var cardPrediction: CardView
    private lateinit var tvPredictionSummary: TextView


    private val statisticRepo = StatisticRepository()
    private var selectedMode = "chi"
    private var selectedPeriod = "month"

    override fun getLayoutId(): Int = R.layout.fragment_statistics
    override fun getToolbarTitle(): String? = "Thống kê"
    override fun useToolbar(): Boolean = true
    override fun showBackButton(): Boolean = true
    override fun shouldNavigateToHomeOnBack(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pieChart)
        lineChart = view.findViewById(R.id.chartCompare)
        llSpendingCard = view.findViewById(R.id.llSpendingCard)
        llIncomeCard = view.findViewById(R.id.llIncomeCard)
        tvTotalSpending = view.findViewById(R.id.tvTotalSpending)
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome)
        tvSpendChange = view.findViewById(R.id.tvSpendChange)
        tvIncomeChange = view.findViewById(R.id.tvIncomeChange)
        tvFilter = view.findViewById(R.id.tvFilter)
        val ivArrow = view.findViewById<ImageView>(R.id.ivArrow)
        cardPrediction = view.findViewById(R.id.cardPrediction)
        tvPredictionSummary = view.findViewById(R.id.tvPredictionSummary)


        updateCardSelection()
        fetchStatistics()

        val filterClickListener = View.OnClickListener { showFilterMenu(tvFilter) }

        tvFilter.setOnClickListener(filterClickListener)
        ivArrow.setOnClickListener(filterClickListener)

        llSpendingCard.setOnClickListener {
            selectedMode = "chi"
            updateCardSelection()
            fetchStatistics()
        }

        llIncomeCard.setOnClickListener {
            selectedMode = "thu"
            updateCardSelection()
            fetchStatistics()
        }
    }

    @SuppressLint("ResourceType")
    private fun updateCardSelection() {
        if (selectedMode == "chi") {
            llSpendingCard.setBackgroundResource(R.drawable.bg_card_selected)
            llSpendingCard.cardElevation = 8f
            llIncomeCard.setBackgroundResource(R.drawable.bg_card_unselected)
            llIncomeCard.cardElevation = 0f
        } else {
            llIncomeCard.setBackgroundResource(R.drawable.bg_card_selected)
            llIncomeCard.cardElevation = 8f
            llSpendingCard.setBackgroundResource(R.drawable.bg_card_unselected)
            llSpendingCard.cardElevation = 0f
        }
    }

    private fun fetchStatistics() {
        val userId = SharedPrefManager.getUserId(requireContext())
        if (userId == -1) {
            Log.e("STATISTICS", "User ID not found in SharedPreferences")
            return
        }

        lifecycleScope.launch {
            try {
                val pieData = statisticRepo.getPieData(userId, selectedMode, selectedPeriod)
                Log.d("STATISTICS", "Pie response: $pieData")

                val lineData = statisticRepo.getLineData(userId, selectedMode, selectedPeriod)
                Log.d("STATISTICS", "Line response: $lineData")

                val totalData = statisticRepo.getTotal(userId, selectedMode, selectedPeriod)
                Log.d("STATISTICS", "Summary response: $totalData")

                val prediction = statisticRepo.getPredictedSpending(userId, selectedMode)
                Log.d("STATISTICS", "Prediction: $prediction")

                val formatter = DecimalFormat("#,###")
                val predicted = formatter.format(prediction.predicted)
                val average = formatter.format(prediction.average)

                val percentValue = prediction.percent_change
                val percentText = "%.1f".format(kotlin.math.abs(percentValue))

                val warningText = when {
                    percentValue > 20 -> "⚠️ Chi tiêu có thể tăng $percentText% so với trung bình!"
                    percentValue < -10 -> "✅ Bạn đang tiết kiệm hơn $percentText%!"
                    else -> "🧾 Mọi thứ ổn định, tiếp tục giữ nhịp độ nhé!"
                }


                tvPredictionSummary.text = """
    Dự đoán tháng này: $predicted đ
    Trung bình 3 tháng gần nhất: $average đ
    $warningText
""".trimIndent()

// Đổi màu nền tùy theo phần trăm
                val colorRes = when {
                    prediction.percent_change > 20 -> R.color.warning_red
                    prediction.percent_change < -10 -> R.color.safe_green
                    else -> R.color.neutral_yellow
                }
                cardPrediction.setCardBackgroundColor(ContextCompat.getColor(requireContext(), colorRes))


                updatePieChart(pieData)
                updateLineChart(lineData)
                updateTotalUI(totalData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updatePieChart(data: List<StatisticPieEntry>) {
        val entries = data.mapIndexed { index, it ->
            PieEntry(it.percent, it.category).apply {
                this.data = it.amount
            }
        }

        val dataSet = PieDataSet(entries, "")
        val colors = listOf(
            Color.parseColor("#6BC0E2"),
            Color.parseColor("#6A45DC"),
            Color.parseColor("#EA9F45"),
            Color.parseColor("#F0604D"),
            Color.parseColor("#E26FA3"),
            Color.parseColor("#96AD5C")
        )
        dataSet.colors = colors
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f
        dataSet.setDrawValues(false)

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.legend.isEnabled = false
        pieChart.centerText = if (selectedMode == "thu") "Thu nhập" else "Chi tiêu"
        pieChart.setCenterTextSize(18f)
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.invalidate()

        // Gán marker
        val pieMarker = PieMarkerView(requireContext(), R.layout.custom_marker_pie)
        pieMarker.chartView = pieChart
        pieChart.marker = pieMarker

        // Hiển thị legend
        val legendContainer = requireView().findViewById<FlexboxLayout>(R.id.legendContainer)
        legendContainer.removeAllViews()
        data.forEachIndexed { i, item ->
            val view = layoutInflater.inflate(R.layout.item_legend, legendContainer, false)
            val tvLegend = view.findViewById<TextView>(R.id.tvLegend)
            tvLegend.text = "${item.percent.toInt()}% ${item.category}"
            tvLegend.setTextColor(Color.WHITE)  // Luôn đảm bảo chữ đậm rõ
            view.background.setTint(colors[i % colors.size]) // Đổi viền theo màu lát
            legendContainer.addView(view)
            Log.d("PIE_LEGEND", "Legend ${item.category} - ${item.percent}")
        }
    }





    private fun updateLineChart(data: List<StatisticLineEntry>) {
        val periods = data.groupBy { it.period }
        val lineDataSets = mutableListOf<ILineDataSet>()
        val xLabels = mutableListOf<String>()

        periods.forEach { (period, entries) ->
            val lineEntries = entries.mapIndexed { index, entry ->
                if (xLabels.size <= index) xLabels.add(entry.timeUnit)
                Entry(index.toFloat(), entry.amount)
            }

            val dataSet = LineDataSet(lineEntries, period).apply {
                color = if (period.contains("trước")) Color.rgb(252,179,179) else Color.rgb(209,234,250)
                valueTextColor = Color.BLACK
                setCircleColor(color)
                circleRadius = 4f
                lineWidth = 2f
                setDrawFilled(true)
                fillAlpha = 60
                setDrawValues(false) // Ẩn giá trị số nếu quá nhiều điểm
            }

            lineDataSets.add(dataSet)
        }

        val lineChartData = LineData(lineDataSets)
        lineChart.data = lineChartData

        // Tùy chỉnh trục X để hỗ trợ cuộn ngang
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        xAxis.labelRotationAngle = -45f // xoay nhãn cho đỡ chồng
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        // Cấu hình cuộn ngang
        lineChart.setDragEnabled(true)
        lineChart.setScaleEnabled(false)
        lineChart.setVisibleXRangeMaximum(7f) // hiển thị tối đa 7 ngày cùng lúc (tuỳ chỉnh)
        lineChart.moveViewToX((xLabels.size - 7).toFloat().coerceAtLeast(0f))

        // Tùy chỉnh trục Y
        lineChart.axisRight.isEnabled = false

        lineChart.description = Description().apply { text = "" }
        lineChart.invalidate()

        val markerView = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
        markerView.chartView = lineChart
        lineChart.marker = markerView

    }


    private fun updateTotalUI(total: StatisticTotalEntry) {
        val formatter = DecimalFormat("#,###")
        val amount = formatter.format(total.amount)
        val percent = "${"%.1f".format(total.percentChange)}%"

        if (selectedMode == "chi") {
            tvTotalSpending.text = amount
            tvSpendChange.text = percent
        } else {
            tvTotalIncome.text = amount
            tvIncomeChange.text = percent
        }
    }

    private fun showFilterMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.statistics_filter_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            val (periodKey, displayText) = when (item.itemId) {
                R.id.menu_day -> "day" to "Hôm nay"
                R.id.menu_week -> "week" to "Tuần này"
                R.id.menu_month -> "month" to "Tháng này"
                R.id.menu_year -> "year" to "Năm này"
                else -> null to null
            }

            periodKey?.let {
                selectedPeriod = it
                tvFilter.text = displayText
                fetchStatistics()
            }

            true
        }

        popup.show()
    }
}
