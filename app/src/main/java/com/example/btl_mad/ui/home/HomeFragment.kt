//package com.example.btl_mad.ui.home
//
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.btl_mad.R
//import com.example.btl_mad.api.RetrofitClient
//import com.example.btl_mad.data.Category
//import com.example.btl_mad.data.Notification
//import com.example.btl_mad.data.Transaction_home
//import com.example.btl_mad.data.statistics.StatisticRepository
//import com.example.btl_mad.ui.BaseFragment
//import com.example.btl_mad.ui.home.adapter.CategoryAdapter
//import com.example.btl_mad.ui.notification.NotificationDialogFragment
//import com.example.btl_mad.data.Fund
//import com.example.btl_mad.ui.fund.AddFundActivity
//import com.example.btl_mad.ui.fund.ListFund
//import com.example.btl_mad.ui.home.adapter.CategorySpendingAdapter
//import com.example.btl_mad.ui.home.adapter.FundsLimitAdapter
//import com.example.btl_mad.ui.home.adapter.RecentTransactionAdapter
//import com.example.btl_mad.ui.main.MainActivity
//import com.example.btl_mad.utils.SharedPrefManager
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.components.Description
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.google.android.material.button.MaterialButtonToggleGroup
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import kotlinx.coroutines.launch
//import retrofit2.Call
//import java.text.DecimalFormat
//
//class HomeFragment : BaseFragment() {
//
//    override fun getLayoutId(): Int = R.layout.fragment_home
//    override fun getToolbarTitle(): String? = null
//    override fun useToolbar(): Boolean = false
//
//    private val statisticRepo = StatisticRepository()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
//        val limitRecycler = view.findViewById<RecyclerView>(R.id.rvLimitSummary)
//        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//        val userJson = sharedPref.getString("user", null)
//
//        var userId = -1
//
//        userJson?.let {
//            val type = object : TypeToken<Map<String, Any>>() {}.type
//            val userMap: Map<String, Any> = Gson().fromJson(it, type)
//            val username = userMap["username"] as? String ?: "Người dùng"
//            userId = (userMap["id"] as? Double)?.toInt() ?: -1
//            tvGreeting.text = "Xin chào, $username"
//        }
//
//        //Hiển thị số dư và tổng chi tháng
//        val tvTotalSpent = view.findViewById<TextView>(R.id.tvTotalSpent)
//        val tvBalance = view.findViewById<TextView>(R.id.tvBalance)
//
//        lifecycleScope.launch {
//            try {
//                val summary = RetrofitClient.apiService.getSpendingSummary(userId)
//                val formatter = DecimalFormat("#,###")
//
//                tvTotalSpent.text = formatter.format(summary.total_spending.toDouble()) + " đ"
//                tvBalance.text = "Số dư: " + formatter.format(summary.current_balance.toDouble()) + " đ"
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(requireContext(), "Không thể tải dữ liệu tổng chi và số dư", Toast.LENGTH_SHORT).show()
//            }
//        }
////Hiển thị thông tin chi tiêu của hũ
//        val recycler = view.findViewById<RecyclerView>(R.id.rvLimitSummary)
//        recycler.layoutManager = LinearLayoutManager(requireContext())
//
//        lifecycleScope.launch {
//            try {
//                val userId = SharedPrefManager.getUserId(requireContext())
//                if (userId == -1) {
//                    Log.e("STATISTICS", "User ID not found in SharedPreferences")
//                }
//
//                if (userId != -1) {
//                    val funds = RetrofitClient.apiService.getFundsByUserId(userId)
//                    recycler.adapter = FundsLimitAdapter(requireContext(), funds)
//                }
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(requireContext(), "Lỗi khi tải hạn mức", Toast.LENGTH_SHORT).show()
//            }
//            val tvViewAllLimits = view.findViewById<TextView>(R.id.tvViewAllLimits)
//            tvViewAllLimits.setOnClickListener {
//                val intent = Intent(requireContext(), MainActivity::class.java)
//                startActivity(intent)
//            }
//        }
//
//
//
//        val btnNotification = view.findViewById<ImageView>(R.id.btnNotification)
//        btnNotification.setOnClickListener {
//            val demoNotifications = listOf(
//                Notification(1, 1, "Ưu đãi mới", "Bạn nhận được voucher 50K", "19:00 19/04/2025"),
//                Notification(2, 1, "Cảnh báo", "Chi tiêu vượt hạn mức tháng 4", "17:35 18/04/2025")
//            )
//            val popup = NotificationDialogFragment(demoNotifications)
//            popup.show(parentFragmentManager, "notification_popup")
//        }
//
////Hiển thị Chi theo phân loại
//        val rv = view.findViewById<RecyclerView>(R.id.rvCategorySpending)
////        val btnGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.btnGroupType)
//        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//
//        lifecycleScope.launch {
//            try {
//                val result = RetrofitClient.apiService.getSpendingByCategory(userId)
//                rv.adapter = CategorySpendingAdapter(result) {
//                    // Mở AddFundActivity khi bấm dấu +
//                    val intent = Intent(requireContext(), AddFundActivity::class.java)
//                    startActivity(intent)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//        view.findViewById<TextView>(R.id.tvViewAllCategories).setOnClickListener {
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
//        }
//
////        categoryRecycler.adapter = CategoryAdapter(emptyList())
//        //Hiển thị danh sách giao dịch gần đây
//        val rvTransactions = view.findViewById<RecyclerView>(R.id.rvRecentTransactions)
//        rvTransactions.layoutManager = LinearLayoutManager(requireContext())
//
//        val btnGroup = view.findViewById<MaterialButtonToggleGroup>(R.id.btnGroupType)
//        val tvViewAll = view.findViewById<TextView>(R.id.tvViewMore)
//
//        // Hàm load giao dịch theo loại
//        fun loadTransactions(type: String) {
//            lifecycleScope.launch {
//                try {
//                    val data = RetrofitClient.apiService.getRecentTransactions(
//                        userId = userId,
//                        type = type,
//                        limit = 5 // hoặc 10 tùy bạn
//                    )
//                    rvTransactions.adapter = RecentTransactionAdapter(data)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//// Mặc định hiển thị loại "chi"
//        if (userId != -1) {
//            try {
//                loadTransactions("chi")
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Toast.makeText(requireContext(), "Lỗi khi tải giao dịch: ${e.message}", Toast.LENGTH_LONG).show()
//            }
//        }
//
//// Khi chọn Chi / Thu
//        btnGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
//            if (isChecked) {
//                val type = when (checkedId) {
//                    R.id.btnChi -> "chi"
//                    R.id.btnThu -> "thu"
//                    else -> "chi"
//                }
//                loadTransactions(type)
//            }
//        }
//
//// Xem thêm → sang MainActivity (tab giao dịch)
//        tvViewAll.setOnClickListener {
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        val chart = view.findViewById<LineChart>(R.id.lineChart)
//        setupChart(chart)
//    }
//
//    private fun setupChart(chart: LineChart) {
//        val entries = listOf(
//            Entry(1f, 400f),
//            Entry(2f, 600f),
//            Entry(3f, 800f),
//            Entry(4f, 500f),
//            Entry(5f, 300f),
//            Entry(6f, 400f)
//        )
//
//        val dataSet = LineDataSet(entries, "Chi tiêu").apply {
//            color = resources.getColor(R.color.blue, null)
//            valueTextColor = resources.getColor(R.color.black, null)
//            circleRadius = 4f
//            setDrawFilled(true)
//        }
//
//        chart.apply {
//            data = LineData(dataSet)
//            description = Description().apply { text = "" }
//            invalidate()
//        }
//    }
//}