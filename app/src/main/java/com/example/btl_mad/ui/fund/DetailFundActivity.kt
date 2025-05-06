package com.example.btl_mad.ui.fund

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.FundDetail
import com.example.btl_mad.data.FundInfo
import com.example.btl_mad.data.TransFund
import com.example.btl_mad.ui.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.Calendar
import com.example.btl_mad.ui.utils.SharedPrefManager


class DetailFundActivity : AppCompatActivity() {
    private lateinit var monthSpinner: Spinner
    private lateinit var monthList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var searchEditText: EditText
    private lateinit var navHome: LinearLayout
    private var month: Int = 0
    private var year: Int = 0
    private var fundId: Int = -1
    private var userId: Int = -1
    private var type: String = ""
    private var search: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fund_detail)
        searchEditText = findViewById(R.id.searchTransaction)
        val currentDate = LocalDate.now()
        month = currentDate.monthValue
        year = currentDate.year
        fundId = intent.getIntExtra("FUND_ID", -1)
        userId = SharedPrefManager.getUserId(this)


        monthSpinner = findViewById(R.id.monthSpinner)

        // Tạo danh sách các giá trị tháng
        monthList = ArrayList()
        monthList.add("Tháng này")
        monthList.add("Tháng trước")
        monthList.add("Tháng trước nữa")

        // Tạo ArrayAdapter với dữ liệu
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, monthList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = adapter


        // Lắng nghe sự kiện khi người dùng chọn tháng
        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMonth = monthList[position]
                handleMonthSelection(selectedMonth)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Không làm gì khi không có sự lựa chọn
            }
        }

        if (fundId != -1) {
            // Gọi API để lấy danh sách giao dịch của quỹ
            getTransactions(fundId, month, year, type, search)
            getFundInfo(fundId, month, year)
        }

        navHome = findViewById(R.id.nav_home)
        navHome.setOnClickListener {
            navigateToHome()
        }
    }

    fun navigateToHome() {
        val intent = Intent(this@DetailFundActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun handleMonthSelection(selectedMonth: String) {
        println(selectedMonth)

        // Lấy tháng và năm hiện tại
        val calendar = Calendar.getInstance()
        month = calendar.get(Calendar.MONTH) + 1  // Tháng (0-11), cộng 1 để có tháng 1-12
        year = calendar.get(Calendar.YEAR)  // Năm hiện tại

        when (selectedMonth) {
            "Tháng này" -> {
                // Không thay đổi tháng và năm
                getTransactions(fundId, month, year, type, search)
                getFundInfo(fundId, month, year)
            }
            "Tháng trước" -> {
                // Lùi lại 1 tháng
                month -= 1
                if (month < 1) {
                    month = 12
                    year -=1
                }
                getTransactions(fundId, month, year, type, search)
                getFundInfo(fundId, month, year)
            }
            "Tháng trước nữa" -> {
                month -= 2
                if (month < 1) {
                    month += 12
                    year -= 1  // Điều chỉnh năm khi lùi về tháng trước nữa
                }
                getTransactions(fundId, month, year, type, search)
                getFundInfo(fundId, month, year)
            }
        }
    }

    fun onSearchClick(view: View) {
        val searchKeyword = searchEditText.text.toString().trim()
        if (fundId != -1) {
            getTransactions(fundId, month, year, type, searchKeyword)
        }
    }

    fun onTypeClick(view: View) {
        val searchKeyword = searchEditText.text.toString().trim()
        when (view.id) {
            R.id.totalSpending -> {
                type = "chi"
                getTransactions(fundId, month, year, type, searchKeyword)
            }
            R.id.totalIncome -> {
                type = "thu"
                getTransactions(fundId, month, year, type, searchKeyword)
            }
        }
    }


    private fun getTransactions(fundId: Int, month: Int, year: Int, type: String, search: String) {
        // Gọi API thông qua RetrofitClient
        RetrofitClient.apiService.getTransactionsFund(fundId, month, year, type, search).enqueue(object : Callback<List<TransFund>> {
            override fun onResponse(call: Call<List<TransFund>>, response: Response<List<TransFund>>) {
                if (response.isSuccessful && response.body() != null) {
                    val transactions = response.body()!!
                    // Chuyển đổi dữ liệu từ API thành SpendingItem (hoặc bạn có thể sử dụng trực tiếp TransFund)
                    val spendingList = transactions.map { transaction ->
                        SpendingItem(
                            name = transaction.note,
                            amount = "${transaction.amount} đ",
                            color = if (transaction.in_out_budget == "thu") "#90E556" else "#E56E56"
                        )
                    }
                    // Cập nhật RecyclerView với dữ liệu từ API
                    setupRecyclerView(spendingList)
                } else {
                    Toast.makeText(this@DetailFundActivity, "Không có giao dịch", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TransFund>>, t: Throwable) {
                Toast.makeText(this@DetailFundActivity, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFundInfo(fundId: Int, month: Int, year: Int) {
        // Gọi API sử dụng Retrofit
        RetrofitClient.apiService.getFundInfo(fundId, month, year).enqueue(object : Callback<List<FundInfo>> {

            override fun onResponse(call: Call<List<FundInfo>>, response: Response<List<FundInfo>>) {
                if (response.isSuccessful && response.body() != null) {
                    val fundInfoList = response.body() // Lấy danh sách các FundInfo

                    // Kiểm tra danh sách không rỗng
                    if (fundInfoList.isNullOrEmpty()) {
                        val formatter = DecimalFormat("#,###")
                        findViewById<TextView>(R.id.totalSpendingValue).text = formatter.format(0) + " đ"
                        findViewById<TextView>(R.id.totalIncomeValue).text = formatter.format(0) + " đ"
                        Toast.makeText(this@DetailFundActivity, "Hũ này chưa có giao dịch", Toast.LENGTH_SHORT).show()
                    } else {
                        val fundInfo = fundInfoList.firstOrNull()

                        if (fundInfo != null) {
                            val totalSpending = fundInfo.total_spent ?: 0f  // Nếu total_spent là null, gán 0f
                            val totalIncome = fundInfo.total_income ?: 0f  // Nếu budget là null, gán 0f
                            val formatter = DecimalFormat("#,###")

                            findViewById<TextView>(R.id.totalSpendingValue).text = formatter.format(totalSpending) + " đ"
                            findViewById<TextView>(R.id.totalIncomeValue).text = formatter.format(totalIncome) + " đ"
                            findViewById<TextView>(R.id.textView).text = fundInfo.name
                        } else {
                            Toast.makeText(this@DetailFundActivity, "Hũ này chưa có giao dịch", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this@DetailFundActivity, "Lỗi API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FundInfo>>, t: Throwable) {
                Toast.makeText(this@DetailFundActivity, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun setupRecyclerView(spendingList: List<SpendingItem>) {
        val adapter = SpendingAdapter(spendingList)
        val recyclerView: RecyclerView = findViewById(R.id.spendingList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    fun goBack(view: android.view.View){
        finish()
    }

    fun openEditDialog(view: View) {
        RetrofitClient.apiService.getFundInfo(fundId, month, year).enqueue(object : Callback<List<FundInfo>> {

            override fun onResponse(call: Call<List<FundInfo>>, response: Response<List<FundInfo>>) {
                if (response.isSuccessful && response.body() != null) {
                    val fundInfoList = response.body()

                    // Kiểm tra danh sách không rỗng
                    if (fundInfoList.isNullOrEmpty()) {
                        Toast.makeText(this@DetailFundActivity, "Không có thông tin", Toast.LENGTH_SHORT).show()
                    } else {
                        val fundInfo = fundInfoList.firstOrNull()

                        if (fundInfo != null) {
                            val bundle = Bundle()
                            bundle.putInt("fundId", fundInfo.id)
                            bundle.putInt("userId", userId)
                            bundle.putString("icon", fundInfo.icon)
                            bundle.putString("name", fundInfo.name)
                            bundle.putInt("budget", fundInfo.budget)
                            val editDialog = EditBottomSheetDialogFragment()
                            editDialog.arguments = bundle
                            editDialog.show(supportFragmentManager, editDialog.tag)
                        } else {
                            // Trường hợp fundInfo là null, xử lý khi không có thông tin chi tiêu
                            Toast.makeText(this@DetailFundActivity, "Hũ này chưa có giao dịch", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(this@DetailFundActivity, "Lỗi API", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<FundInfo>>, t: Throwable) {
                Toast.makeText(this@DetailFundActivity, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
            }

        })
    }
}

// Lớp để chứa thông tin của mỗi mục chi tiêu
data class SpendingItem(val name: String, val amount: String, val color: String)

// Adapter cho RecyclerView
class SpendingAdapter(private val spendingList: List<SpendingItem>) :
    RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder>() {

    // ViewHolder để lưu trữ các view của mỗi item
    class SpendingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemAmount: TextView = view.findViewById(R.id.itemAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return SpendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        val item = spendingList[position]

        // Cập nhật dữ liệu cho mỗi mục
        holder.itemName.text = item.name
        holder.itemAmount.text = item.amount
        val color = Color.parseColor(item.color)
        holder.itemName.setTextColor(color) // Màu cho tên
        holder.itemAmount.setTextColor(color) // Màu cho số tiền
    }

    override fun getItemCount(): Int = spendingList.size
}
