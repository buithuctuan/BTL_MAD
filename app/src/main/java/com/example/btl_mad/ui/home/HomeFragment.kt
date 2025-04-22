package com.example.btl_mad.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.data.Category
import com.example.btl_mad.data.Notification
import com.example.btl_mad.data.Transaction
import com.example.btl_mad.data.statistics.StatisticRepository
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.ui.home.adapter.CategoryAdapter
import com.example.btl_mad.ui.home.adapter.TransactionAdapter
import com.example.btl_mad.ui.notification.NotificationDialogFragment
import com.example.btl_mad.ui.home.adapter.SpendingLimitAdapter
import com.example.btl_mad.ui.home.adapter.SpendingLimitItem
import com.example.btl_mad.data.Fund
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class HomeFragment : BaseFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_home
    override fun getToolbarTitle(): String? = null
    override fun useToolbar(): Boolean = false

    private val statisticRepo = StatisticRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        val tvTotalSpending = view.findViewById<TextView>(R.id.tvTotalSpendingHome)
        val limitRecycler = view.findViewById<RecyclerView>(R.id.rvLimitSummary)
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userJson = sharedPref.getString("user", null)

        var userId = -1

        userJson?.let {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val userMap: Map<String, Any> = Gson().fromJson(it, type)
            val username = userMap["username"] as? String ?: "Người dùng"
            userId = (userMap["id"] as? Double)?.toInt() ?: -1
            tvGreeting.text = "Xin chào, $username"
        }

        lifecycleScope.launch {
            try {
                val summary = statisticRepo.getTotal(userId, "chi", "month")
                val formatter = DecimalFormat("#,###")
                tvTotalSpending.text = formatter.format(summary.amount) + " đ"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        lifecycleScope.launch {
            try {
                val result: List<Fund> = statisticRepo.getTransactionTypes(userId)
                val spendingSummary = result.map {
                    SpendingLimitItem(
                        category = it.name,
                        limit = it.spending_limit,
                        spent = it.total_expenses
                    )
                }
                limitRecycler.layoutManager = LinearLayoutManager(requireContext())
                limitRecycler.adapter = SpendingLimitAdapter(spendingSummary)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val btnNotification = view.findViewById<ImageView>(R.id.btnNotification)
        btnNotification.setOnClickListener {
            val demoNotifications = listOf(
                Notification(1, 1, "Ưu đãi mới", "Bạn nhận được voucher 50K", "19:00 19/04/2025"),
                Notification(2, 1, "Cảnh báo", "Chi tiêu vượt hạn mức tháng 4", "17:35 18/04/2025")
            )
            val popup = NotificationDialogFragment(demoNotifications)
            popup.show(parentFragmentManager, "notification_popup")
        }

        val categoryRecycler = view.findViewById<RecyclerView>(R.id.rvCategories)
        categoryRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecycler.adapter = CategoryAdapter(emptyList())

        val transactionRecycler = view.findViewById<RecyclerView>(R.id.rvTransactions)
        transactionRecycler.layoutManager = LinearLayoutManager(requireContext())
        transactionRecycler.adapter = TransactionAdapter(emptyList())

        val chart = view.findViewById<LineChart>(R.id.lineChart)
        setupChart(chart)
    }

    private fun setupChart(chart: LineChart) {
        val entries = listOf(
            Entry(1f, 400f),
            Entry(2f, 600f),
            Entry(3f, 800f),
            Entry(4f, 500f),
            Entry(5f, 300f),
            Entry(6f, 400f)
        )

        val dataSet = LineDataSet(entries, "Chi tiêu").apply {
            color = resources.getColor(R.color.blue, null)
            valueTextColor = resources.getColor(R.color.black, null)
            circleRadius = 4f
            setDrawFilled(true)
        }

        chart.apply {
            data = LineData(dataSet)
            description = Description().apply { text = "" }
            invalidate()
        }
    }
}