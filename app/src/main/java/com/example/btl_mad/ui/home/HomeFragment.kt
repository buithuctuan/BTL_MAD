package com.example.btl_mad.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.data.Category
import com.example.btl_mad.data.Notification
import com.example.btl_mad.data.Transaction
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.ui.home.adapter.CategoryAdapter
import com.example.btl_mad.ui.home.adapter.TransactionAdapter
import com.example.btl_mad.ui.notification.NotificationDialogFragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : BaseFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_home
    override fun getToolbarTitle(): String? = null
    override fun useToolbar(): Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 Lời chào người dùng
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userJson = sharedPref.getString("user", null)

        userJson?.let {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val userMap: Map<String, Any> = Gson().fromJson(it, type)
            val username = userMap["username"] as? String ?: "Người dùng"
            tvGreeting.text = "Xin chào, $username"
        }

        // 🔹 Xử lý nút thông báo
        val btnNotification = view.findViewById<ImageView>(R.id.btnNotification)
        btnNotification.setOnClickListener {
            // ⚠️ Demo danh sách tạm, bạn có thể gọi API thực tại đây
            val demoNotifications = listOf(
                Notification(1, 1, "Ưu đãi mới", "Bạn nhận được voucher 50K", "19:00 19/04/2025"),
                Notification(2, 1, "Cảnh báo", "Chi tiêu vượt hạn mức tháng 4", "17:35 18/04/2025")
            )
            val popup = NotificationDialogFragment(demoNotifications)
            popup.show(parentFragmentManager, "notification_popup")
        }

        // 🔹 Danh sách danh mục chi tiêu
        val categoryRecycler = view.findViewById<RecyclerView>(R.id.rvCategories)
        val categories = listOf(
            Category("Cần thiết", 1_000_000, 55),
            Category("Đào tạo", 1_000_000, 10),
            Category("Hưởng thụ", 1_000_000, 10),
            Category("Tự do", 1_000_000, 5)
        )
        categoryRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecycler.adapter = CategoryAdapter(categories)

        // 🔹 Danh sách giao dịch gần đây
        val transactionRecycler = view.findViewById<RecyclerView>(R.id.rvTransactions)
        val transactions = listOf(
            Transaction("Tiền siêu thị", 250_000, "03/06/23"),
            Transaction("Tiền cafe", 50_000, "04/06/23"),
            Transaction("Đi chợ", 300_000, "05/06/23")
        )
        transactionRecycler.layoutManager = LinearLayoutManager(requireContext())
        transactionRecycler.adapter = TransactionAdapter(transactions)

        // 🔹 Biểu đồ chi tiêu
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
