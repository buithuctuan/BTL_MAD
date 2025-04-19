package com.example.btl_mad.ui.fund

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import java.text.DecimalFormat


class DetailFundActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spending_history)

        val totalSpending = 100000
        val formatter = DecimalFormat("#,###")
        val formattedSpending = formatter.format(totalSpending) + " đ"
        val totalSpendingTextView: TextView = findViewById(R.id.totalSpendingValue)
        totalSpendingTextView.text = formattedSpending

        val totalIncomeValue = 100000
        val formattedIncome = formatter.format(totalIncomeValue) + " đ"
        val totalIncomeTextView: TextView = findViewById(R.id.totalIncomeValue)
        totalIncomeTextView.text = formattedIncome

        // Khởi tạo danh sách chi tiêu
        val spendingList = listOf(
            SpendingItem("Tiền siêu thị", "250.000 đ", "#E56E56"),
            SpendingItem("Học tiếng anh",  "250.000 đ", "#E56E56"),
            SpendingItem("Tiền tiết kiệm",  "250.000 đ", "#E56E56"),
            SpendingItem("Tiền siêu thị",  "250.000 đ", "#90E556"),
            SpendingItem("Học tiếng anh",  "250.000 đ", "#90E556"),
            SpendingItem("Tiền tiết kiệm",  "250.000 đ", "#90E556"),
            SpendingItem("Du lịch Mộc Châu",  "250.000 đ", "#90E556")
        )

        // Tạo RecyclerView và thiết lập Adapter
        val recyclerView: RecyclerView = findViewById(R.id.spendingList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = SpendingAdapter(spendingList)
    }

    fun goBack(view: android.view.View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // Trong Activity hoặc Fragment của bạn
    fun openEditDialog(view: View) {
        // Tạo và hiển thị BottomSheetDialogFragment
        val editDialog = EditBottomSheetDialogFragment()
        editDialog.show(supportFragmentManager, editDialog.tag)
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
