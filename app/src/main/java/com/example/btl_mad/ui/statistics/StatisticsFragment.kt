package com.example.btl_mad.ui.statistics

import CustomMarkerView
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.btl_mad.R
import com.example.btl_mad.data.Funds_home
import com.example.btl_mad.data.statistics.SpendingLimitEntry
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
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.widget.Button
import com.example.btl_mad.ui.transactionhistory.SpendingHistory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
    private var fromDate: String? = null
    private var toDate: String? = null
    private var isCustomDate = false


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

        val btnMoreDetails = view.findViewById<Button>(R.id.btnMoreDetails)
        btnMoreDetails.setOnClickListener {
            val intent = Intent(requireContext(), SpendingHistory::class.java)
            intent.putExtra("mode", selectedMode) // truyền "chi" hoặc "thu"
            startActivity(intent)
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
        Log.d("DEBUG", "fetchStatistics userId = $userId")
        if (userId == -1) {
            Log.e("STATISTICS", "User ID not found in SharedPreferences")
            return
        }

        lifecycleScope.launch {
            try {
                Log.d("STATISTICS", "Calling getPieData")
                val pieData = if (selectedPeriod == "custom")
                    statisticRepo.getPieDataCustom(userId, selectedMode, fromDate!!, toDate!!)
                else
                    statisticRepo.getPieData(userId, selectedMode, selectedPeriod)

                Log.d("STATISTICS", "PieData received: $pieData")

                Log.d("STATISTICS", "Calling getLineData")
                val lineData = if (selectedPeriod == "custom")
                    statisticRepo.getLineDataCustom(userId, selectedMode, fromDate!!, toDate!!)
                else
                    statisticRepo.getLineData(userId, selectedMode, selectedPeriod)
                Log.d("STATISTICS", "LineData received: $lineData")

                Log.d("STATISTICS", "Calling getTotal")
                val totalData = if (isCustomDate) {
                    statisticRepo.getTotalCustom(userId, selectedMode, fromDate!!, toDate!!)
                } else {
                    statisticRepo.getTotal(userId, selectedMode, selectedPeriod)
                }

                Log.d("STATISTICS", "TotalData received: $totalData")

                Log.d("STATISTICS", "Calling getPredictedSpending")
                //Gọi API / repo để lấy dữ liệu dự đoán
                val prediction = statisticRepo.getPredictedSpending(userId, "chi")
                Log.d("STATISTICS", "Prediction received: $prediction")

                updatePieChart(pieData)
                updateLineChart(lineData)
                updateTotalUI(totalData)

                //Định dạng dữ liệu cho hiển thị
                val formatter = DecimalFormat("#,###")
                val predicted = formatter.format(prediction.predicted)
                val average = formatter.format(prediction.weighted_average)
                //Tính phần trăm thay đổi
                val percentValue = prediction.percent_change
                val percentText = "%.1f".format(kotlin.math.abs(percentValue))
                //Sinh thông điệp cảnh báo
                val warningText = when {
                    percentValue > 20 -> "Chi tiêu có thể tăng $percentText% so với trung bình!"
                    percentValue < -10 -> "Bạn đang tiết kiệm hơn $percentText%!"
                    else -> "Mọi thứ ổn định, tiếp tục giữ nhịp độ nhé!"
                }
                //Hiển thị đoạn dự đoán lên giao diện
                tvPredictionSummary.text = """
Dự đoán tháng này: $predicted đ
Trung bình 3 tháng gần nhất: $average đ
$warningText
""".trimIndent()
                //Đổi màu nền của cardPrediction để cảnh báo trực quan:
                val colorRes = when {
                    percentValue > 20 -> R.color.warning_red
                    percentValue < -10 -> R.color.safe_green
                    else -> R.color.neutral_yellow
                }

                cardPrediction.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), colorRes)
                )

                Log.d("STATISTICS", "Calling getFunds")
                val funds = statisticRepo.getFunds(userId)
                Log.d("STATISTICS", "Funds received: $funds")
                updateLimitCardsFromFunds(funds)

            } catch (e: Exception) {
                Log.e("STATISTICS", "Exception: ${e.localizedMessage}", e)
                e.printStackTrace()
            }
            val response = statisticRepo.getMonthlyIncomeVsSpending(userId)
            val incomeData = response.first
            val spendingData = response.second
            updateIncomeVsSpendingChart(incomeData, spendingData)
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
        val lineDataSets = mutableListOf<ILineDataSet>()
        val xLabels = mutableListOf<String>()

        if (selectedPeriod == "custom") {
            // Trường hợp khoảng tùy chọn: chỉ 1 đường line
            val entries = data.mapIndexed { index, entry ->
                xLabels.add(entry.timeUnit)
                Entry(index.toFloat(), entry.amount)
            }

            val dataSet = LineDataSet(entries, "Chi tiêu").apply {
                color = Color.rgb(102, 178, 255)
                valueTextColor = Color.BLACK
                setCircleColor(color)
                circleRadius = 4f
                lineWidth = 2f
                setDrawFilled(true)
                fillAlpha = 60
                setDrawValues(false)
            }

            lineDataSets.add(dataSet)
        } else {
            // Trường hợp mặc định: so sánh các kỳ
            val periods = data.groupBy { it.period }

            periods.forEach { (period, entries) ->
                val lineEntries = entries.mapIndexed { index, entry ->
                    if (xLabels.size <= index) xLabels.add(entry.timeUnit)
                    Entry(index.toFloat(), entry.amount)
                }

                val color = if (period.contains("trước")) Color.rgb(252, 179, 179) else Color.rgb(209, 234, 250)
                val dataSet = LineDataSet(lineEntries, period).apply {
                    this.color = color
                    valueTextColor = Color.BLACK
                    setCircleColor(color)
                    circleRadius = 4f
                    lineWidth = 2f
                    setDrawFilled(true)
                    fillAlpha = 60
                    setDrawValues(false)
                }

                lineDataSets.add(dataSet)
            }
        }

        val lineChartData = LineData(lineDataSets)
        lineChart.data = lineChartData

        // Tùy chỉnh trục X để hỗ trợ cuộn ngang
        val xAxis = lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        xAxis.labelRotationAngle = -45f
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        lineChart.setDragEnabled(true)
        lineChart.setScaleEnabled(false)
        lineChart.setVisibleXRangeMaximum(7f)
        lineChart.moveViewToX((xLabels.size - 7).toFloat().coerceAtLeast(0f))

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
                R.id.menu_custom -> {
                    showDateRangePicker()
                    return@setOnMenuItemClickListener true
                }
                else -> null to null
            }

            periodKey?.let {
                selectedPeriod = it
                isCustomDate = false
                tvFilter.text = displayText
                fetchStatistics()
            }

            true
        }

        popup.show()
    }

    private fun showDateRangePicker() {
        val now = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val datePickerFrom = DatePickerDialog(requireContext(), { _, y, m, d ->
            val calFrom = Calendar.getInstance().apply { set(y, m, d) }
            fromDate = dateFormat.format(calFrom.time)

            val datePickerTo = DatePickerDialog(requireContext(), { _, y2, m2, d2 ->
                val calTo = Calendar.getInstance().apply { set(y2, m2, d2) }
                toDate = dateFormat.format(calTo.time)

                isCustomDate = true
                selectedPeriod = "custom"
                tvFilter.text = "Tùy chọn: $fromDate - $toDate"
                fetchStatistics()

            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePickerTo.setTitle("Chọn ngày kết thúc")
            datePickerTo.show()

        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))

        datePickerFrom.setTitle("Chọn ngày bắt đầu")
        datePickerFrom.show()
    }


    private fun updateLimitCardsFromFunds(data: List<Funds_home>) {
        val container = requireView().findViewById<LinearLayout>(R.id.limitContainer)
        container.removeAllViews()
        val formatter = DecimalFormat("#,###")

        data.forEach { fund ->
            val view = layoutInflater.inflate(R.layout.item_limit_card, container, false)
            val tvName = view.findViewById<TextView>(R.id.tvCategoryName)
            val tvSummary = view.findViewById<TextView>(R.id.tvLimitSummary)
            val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

            tvName.text = fund.name
            val percent = if (fund.spending_limit == 0) 0
            else (fund.total_expenses * 100 / fund.spending_limit)

            tvSummary.text = "Đã chi ${formatter.format(fund.total_expenses)} / ${formatter.format(fund.spending_limit)} đ"

            val layerDrawable = progressBar.progressDrawable as LayerDrawable
            val progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)

            when {
                percent >= 100 -> {
                    tvSummary.setTextColor(Color.RED)
                    tvSummary.append(" ⚠ Vượt hạn mức!")
                    progressDrawable.setTint(Color.RED)
                }
                percent >= 80 -> {
                    tvSummary.setTextColor(Color.parseColor("#FFA500")) // Cam
                    tvSummary.append(" ⚠ Sắp vượt!")
                    progressDrawable.setTint(Color.parseColor("#FFA500"))
                }
                else -> {
                    tvSummary.setTextColor(Color.BLACK)
                    progressDrawable.setTint(ContextCompat.getColor(requireContext(), R.color.blue))
                }
            }


            progressBar.max = 100
            progressBar.progress = percent.coerceAtMost(100)

            container.addView(view)
        }
    }
    private fun updateIncomeVsSpendingChart(
        incomeData: List<StatisticLineEntry>,
        spendingData: List<StatisticLineEntry>
    ) {
        val xLabels = (1..12).map { "Tháng $it" }

        val incomeEntries = incomeData.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.amount)
        }
        val spendingEntries = spendingData.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.amount)
        }

        val incomeDataSet = LineDataSet(incomeEntries, "Thu nhập").apply {
            color = Color.rgb(76, 175, 80) // xanh lá
            setCircleColor(color)
            lineWidth = 2f
            setDrawValues(false)
            setDrawFilled(true)
            fillAlpha = 60
        }

        val spendingDataSet = LineDataSet(spendingEntries, "Chi tiêu").apply {
            color = Color.rgb(244, 67, 54) // đỏ
            setCircleColor(color)
            lineWidth = 2f
            setDrawValues(false)
            setDrawFilled(true)
            fillAlpha = 60
        }

        val lineData = LineData(listOf(incomeDataSet, spendingDataSet))

        val chart = requireView().findViewById<LineChart>(R.id.chartIncomeVsSpending)
        chart.data = lineData

        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(xLabels)
            granularity = 1f
            labelRotationAngle = -45f
            setDrawGridLines(false)
        }

        chart.axisRight.isEnabled = false
        chart.description = Description().apply { text = "Thu vs Chi theo tháng" }
        chart.setVisibleXRangeMaximum(12f)
        chart.invalidate()

        val markerView = CustomMarkerView(requireContext(), R.layout.custom_marker_view)
        markerView.chartView = chart
        chart.marker = markerView
    }





}
