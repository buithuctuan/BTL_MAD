package com.example.btl_mad.ui.transactionhistory

import android.annotation.SuppressLint
import com.example.btl_mad.R

import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.api.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.btl_mad.data.Transaction
import com.example.btl_mad.data.Fund
import com.example.btl_mad.data.TransactionRequest
import com.example.btl_mad.ui.utils.SharedPrefManager
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale



class SpendingHistory : AppCompatActivity() {
    // Khởi tạo filter cho lọc các giao dịch theo loại giao dịch
    val checkboxStates = mutableMapOf<String, Pair<Int, Boolean>>(
        "Cần thiết" to Pair(1, true),
        "Đào tạo" to Pair(2, true),
        "Hưởng thụ" to Pair(3, true),
        "Tiết kiệm" to Pair(4, true),
        "Từ thiện" to Pair(5, true)
    )
    val checkboxDateStates = mutableMapOf(
        "Hôm nay" to true,
        "Tuần này" to false,
        "Tháng này" to false,
        "Tuần trước" to false,
        "Tháng trước" to false,
        "Thời gian khác" to false
    )

    var spendingView = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spending_history)
        println(checkboxDateStates)
        val totalSpending = 100000
        val formatter = DecimalFormat("#,###")
        val formattedSpending = formatter.format(totalSpending) + " đ"
        val totalSpendingTextView: TextView = findViewById(R.id.totalSpendingValue)
        totalSpendingTextView.text = formattedSpending

        val totalIncomeValue = 100000
        val formattedIncome = formatter.format(totalIncomeValue) + " đ"
        val totalIncomeTextView: TextView = findViewById(R.id.totalIncomeValue)
        totalIncomeTextView.text = formattedIncome

        // Khoi tao danh sach loai giao dich
        fetchTransactionTypes()
        // Khởi tạo danh sách
        updateTransactionList()

        // cập nhật bộ lọc ngày ra màn hình
        updateDateFilterDisplayField()

    }

    fun goBack(view: android.view.View) {
        finish()
    }

    // Hàm chuyển từ danh sách chi sang danh sách thu
    fun changeToIncomeView(view: View) {
        spendingView = false
        updateTransactionList()
    }

    // Hàm chuyển từ danh sách chi sang danh sách thu
    fun changeToSpendingView(view: View) {
        spendingView = true
        updateTransactionList()
    }

    fun updateTransactionList() {
        // Tạo RecyclerView và thiết lập Adapter
        val recyclerView: RecyclerView = findViewById(R.id.spendingList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Gọi API để lấy danh sách giao dịch
        val timeRange = when (checkboxDateStates.entries.find { it.value }?.key) {
            "Hôm nay" -> "today"
            "Tuần này" -> "this_week"
            "Tháng này" -> "this_month"
            "Tuần trước" -> "last_week"
            "Tháng trước" -> "last_month"
            else -> ""
        }
        val filteredIds = checkboxStates.filter { it.value.second }  // Lọc theo giá trị thứ 2 (true)
            .map { it.value.first }
        val transactionTypeIdsChose = listOf(*filteredIds.toTypedArray())
        val userId = SharedPrefManager.getUserId(this) // ID người dùng
        val inOutBudget = if (spendingView) "chi" else "thu" // Loại giao dịch (chi tiêu)
        val transactionTypeIds = transactionTypeIdsChose // Danh sách ID loại giao dịch
        val transactionReq = TransactionRequest(user_id =userId, in_out_budget = inOutBudget, time_range = timeRange, transaction_type_ids = transactionTypeIds)
        Log.d("TransactionRequest", "User ID: $transactionReq")
        val call = RetrofitClient.apiService.getListTransactions(
            transactionReq
        )

        call.enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                if (response.isSuccessful) {
                    val transactions = response.body()
                    if (transactions != null) {
                        // Cập nhật dữ liệu vào RecyclerView
                        val adapter = SpendingAdapter(transactions, this@SpendingHistory)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Log.e("API Error", "Response error API getlist: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Log.e("API Error", "Error fetching transactions: ${t.message}")
            }
        })
    }


    fun showFilterSpendingDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.filter_spending_dialog, null)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        val categoryList = dialogView.findViewById<LinearLayout>(R.id.categoryList)
        categoryList.removeAllViews()
        // Duyệt qua danh sách checkboxStates và tạo CheckBox động
        for ((category, pair) in checkboxStates) {
            val checkBox = CheckBox(this)
            checkBox.text = category
            checkBox.isChecked = pair.second

            // Lắng nghe sự thay đổi trạng thái của checkbox
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                checkboxStates[category] = Pair(pair.first, isChecked)
                Log.d("FilterCheckbox", "${buttonView.text} = $isChecked")
            }

            // Thêm CheckBox vào LinearLayout
            categoryList.addView(checkBox)
        }
        // Chiếm nửa màn hình
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height =
                (resources.displayMetrics.heightPixels * 0.5).toInt()
            bottomSheet?.setBackgroundResource(R.drawable.filter_dialog)
        }
        dialog.setOnDismissListener{
            updateTransactionList()
        }
        // Sự kiện nút đóng
        dialogView.findViewById<ImageButton>(R.id.closeButton)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // hàm thay đổi hiển thị bộ lọc thời gian
    private fun updateDateFilterDisplayField() {
        val filterCalendar = findViewById<TextView>(R.id.filterCalendar)
        val trueDates = checkboxDateStates.filter { it.value }.keys
        trueDates.firstOrNull()?.let { selectedText ->
            filterCalendar.text = selectedText
        }
    }

    fun showFilterDateDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.filter_date_dialog, null)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)

        // Cho phép đóng dialog khi chạm bên ngoài
        dialog.setCancelable(true)  // Cho phép đóng dialog bằng cách chạm bên ngoài
        dialog.setCanceledOnTouchOutside(true)

        val categoryList = dialogView.findViewById<LinearLayout>(R.id.dateList)
        // Lặp qua các checkbox và gán trạng thái từ biến toàn cục

        val checkBoxList = mutableListOf<CheckBox>() // lưu tất cả checkbox để dễ xử lý

        for (i in 0 until categoryList.childCount) {
            val child = categoryList.getChildAt(i)
            if (child is CheckBox) {
                val label = child.text.toString()
                checkBoxList.add(child)
                child.isChecked = checkboxDateStates[label] ?: true

                child.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        // Gán trạng thái cho checkbox hiện tại
                        checkboxDateStates[buttonView.text.toString()] = true
                        // Uncheck tất cả các ô khác
                        for (cb in checkBoxList) {
                            if (cb != buttonView) {
                                cb.isChecked = false
                                checkboxDateStates[cb.text.toString()] = false

                            }
                        }
                    }
                    Log.d("FilterCheckbox", checkboxDateStates.toString())
                }
            }
        }
        // Chiếm nửa màn hình
        dialog.setOnShowListener {
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height =
                (resources.displayMetrics.heightPixels * 0.5).toInt()
            bottomSheet?.setBackgroundResource(R.drawable.filter_dialog)
        }

        // Sự kiện nút đóng
        dialogView.findViewById<ImageButton>(R.id.closeButton)?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setOnDismissListener {
            updateDateFilterDisplayField()
            updateTransactionList()
        }
        dialog.show()
    }


    fun showDetailTransaction(item: Transaction){
        val dialogView = layoutInflater.inflate(R.layout.transaction_detail, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        // Cho phép đóng dialog khi chạm bên ngoài
        dialog.setCancelable(true)  // Cho phép đóng dialog bằng cách chạm bên ngoài
        dialog.setCanceledOnTouchOutside(true)

        // Chiếm nửa màn hình
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = (resources.displayMetrics.heightPixels * 0.3).toInt()
            bottomSheet?.setBackgroundResource(R.drawable.filter_dialog)
        }

        // Truyền dữ liệu từ item vào các view trong dialog
        // Cập nhật tên mục
        dialogView.findViewById<TextView>(R.id.itemName)?.text = item.note
        // Cập nhật loại mục
        dialogView.findViewById<TextView>(R.id.itemCategory)?.text = item.transaction_type_name
        // Cập nhật số tiền
        dialogView.findViewById<TextView>(R.id.itemAmount)?.text = item.amount.toString()
        // Cập nhật nút xóa
        dialogView.findViewById<Button>(R.id.deleteButton)?.setOnClickListener{
            showConfirmDeleteTransaction(item)
        }
        // Cập nhật màu sắc vòng tròn
//        val imageResId = when (item.color_code) {
//            "can_thiet" -> R.drawable.can_thiet
//            "dao_tao" -> R.drawable.dao_tao
//            "tiet_kiem" -> R.drawable.tiet_kiem
//            "huong_thu" -> R.drawable.huong_thu
//            else -> R.drawable.icon_filter
//        }
        dialogView.findViewById<ImageView>(R.id.colorCircle)?.setImageResource(R.drawable.icon_filter)
        // Cập nhật ngày giao dịch
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dialogView.findViewById<TextView>(R.id.date_transaction)?.text = dateFormat.format(item.dot)

        // Sự kiện nút đóng
        dialogView.findViewById<ImageButton>(R.id.closeButton)?.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    // xác nhận xóa
    private fun showConfirmDeleteTransaction(item: Transaction){
        // Hiển thị AlertDialog xác nhận trước khi xóa
        val dialog = AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa giao dịch này?")
            .setPositiveButton("Xóa") { _, _ ->
                // Thực hiện hành động xóa ở đây, ví dụ:
                // deleteTransaction(item)
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss() // Đóng dialog nếu người dùng chọn hủy
            }
            .create()

        // Cho phép đóng dialog khi chạm bên ngoài
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)

        // Hiển thị dialog
        dialog.show()
    }
    // hàm láy các loại giao dich
    fun fetchTransactionTypes() {
        val userID = 11
        val call = RetrofitClient.apiService.getTransactionTypes(userID) // giả sử userId là 5

        call.enqueue(object : Callback<List<Fund>> {
            override fun onResponse(call: Call<List<Fund>>, response: Response<List<Fund>>) {
                if (response.isSuccessful) {
                    val transactionTypes = response.body()
                    if (transactionTypes != null) {
                        // Tạo mảng mới từ dữ liệu API
                        val updatedCheckboxStates = mutableMapOf<String, Pair<Int, Boolean>>()
                        for (transactionType in transactionTypes) {
                            updatedCheckboxStates[transactionType.name] = Pair(transactionType.id, true) // Mặc định tất cả là true
                        }
                        // Cập nhật lại checkboxStates
                        checkboxStates.clear()  // Xóa trạng thái cũ
                        checkboxStates.putAll(updatedCheckboxStates)  // Thêm trạng thái mới

                        // Gọi hàm để cập nhật lại giao diện nếu cần
                        updateTransactionList()
                    }
                } else {
                    Log.e("API Error", "Response error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Fund>>, t: Throwable) {
                Log.e("API Error", "Error fetching transaction types: ${t.message}")
            }
        })
    }
}



// Adapter cho RecyclerView
class SpendingAdapter(private val spendingList: List<Transaction>,
                      private val context: SpendingHistory ) :
    RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder>() {

    // ViewHolder để lưu trữ các view của mỗi item
    class SpendingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorCircle: ImageView = view.findViewById(R.id.colorCircle)
        val itemName: TextView = view.findViewById(R.id.itemName)
        val itemCategory: TextView = view.findViewById(R.id.itemCategory)
        val itemAmount: TextView = view.findViewById(R.id.itemAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_nam, parent, false)
        return SpendingViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        val item = spendingList[position]

        // Cập nhật dữ liệu cho mỗi mục
        holder.itemName.text = item.note
        holder.itemCategory.text = item.transaction_type_name
        holder.itemAmount.text = item.amount.toString()
//        val imageResId = when (item.color_code) {
//            "can_thiet" -> R.drawable.can_thiet
//            "dao_tao" -> R.drawable.dao_tao
//            "tiet_kiem" -> R.drawable.tiet_kiem
//            "huong_thu" -> R.drawable.huong_thu
//            else -> R.drawable.icon_filter
//        }
        holder.colorCircle.setImageResource(R.drawable.icon_filter)
        holder.itemView.setOnClickListener {
            context.showDetailTransaction(item)
        }
    }

    override fun getItemCount(): Int = spendingList.size
}