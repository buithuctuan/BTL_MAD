package com.example.btl_mad.ui.fund

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.FundResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.btl_mad.ui.utils.SharedPrefManager


class AddFundActivity : AppCompatActivity() {

    private lateinit var iconSelector: LinearLayout
    private lateinit var selectedIcon: ImageView
    private var pos_icon: Int = 1


    private val iconList = listOf(
        R.drawable.fund_icon_1,
        R.drawable.fund_icon_2,
        R.drawable.fund_icon_3,
        R.drawable.fund_icon_4,
        R.drawable.fund_icon_5,
        R.drawable.fund_icon_6,
        R.drawable.fund_icon_7,
        R.drawable.fund_icon_8
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fund)

        iconSelector = findViewById(R.id.iconSelector)
        selectedIcon = findViewById(R.id.selectedIcon)

        iconSelector.setOnClickListener {
            showIconDialog()
        }

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            validateAndAddFund()
        }

    }

    private fun validateAndAddFund() {
        val fundName = findViewById<EditText>(R.id.fundNameEditText).text.toString().trim()
        val limitString = findViewById<EditText>(R.id.limitEditText).text.toString().trim()
        val limit = limitString.toFloatOrNull()

        // Kiểm tra tính hợp lệ của dữ liệu
        if (fundName.isEmpty()) {
            Toast.makeText(this, "Tên loại chi tiêu không được để trống", Toast.LENGTH_SHORT).show()
            return
        }

        if (limit == null || limit <= 0) {
            Toast.makeText(this, "Hạn mức chi tiêu phải là một số hợp lệ và lớn hơn 0", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedIcon = "fund_icon_$pos_icon"
        // Gọi API để thêm hũ chi tiêu nếu mọi thông tin hợp lệ
        addFund(fundName, limit, selectedIcon)
    }

    private fun addFund(fundName: String, limit: Float, selectedIcon: String) {
        val userId =  SharedPrefManager.getUserId(this)


        // Gọi API
        RetrofitClient.apiService.addFund( fundName, userId,selectedIcon, limit).enqueue(object : Callback<FundResponse> {
            override fun onResponse(call: Call<FundResponse>, response: Response<FundResponse>) {
                if (response.isSuccessful) {
                    val fundResponse = response.body()
                    if (fundResponse != null) {
                        if (fundResponse.status == 200) {
                            // Hiển thị thông báo thành công
                            showSuccess()
                        } else {
                            // Hiển thị thông báo lỗi
                            Toast.makeText(this@AddFundActivity, fundResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@AddFundActivity, "Có lỗi khi thêm hũ chi tiêu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FundResponse>, t: Throwable) {
                Toast.makeText(this@AddFundActivity, "Lỗi kết nối API", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSuccess() {
        val dialogView = layoutInflater.inflate(R.layout.success_dialog, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnBack = dialogView.findViewById<Button>(R.id.btnBack)
        val btnCreateNew = dialogView.findViewById<Button>(R.id.btnCreateNew)

        btnBack.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@AddFundActivity, ListFund::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish() // hoặc chuyển về màn hình trước
        }

        btnCreateNew.setOnClickListener {
            dialog.dismiss()
            // reset lại form nếu muốn
            findViewById<EditText>(R.id.fundNameEditText).text.clear()
            findViewById<EditText>(R.id.limitEditText).text.clear()
        }

        dialog.show()
    }


    private fun showIconDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_icon_grid, null)
        val gridView = dialogView.findViewById<GridView>(R.id.gridViewIcons)

        val adapter = object : BaseAdapter() {
            override fun getCount() = iconList.size
            override fun getItem(position: Int) = iconList[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = LayoutInflater.from(this@AddFundActivity)
                    .inflate(R.layout.item_icon, parent, false)
                val image = view.findViewById<ImageView>(R.id.icon)
                image.setImageResource(iconList[position])
                return view
            }
        }

        gridView.adapter = adapter

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        gridView.setOnItemClickListener { _, _, position, _ ->
            selectedIcon.setImageResource(iconList[position])
            pos_icon = position+1
            dialog.dismiss()
        }

        dialog.show()
    }

    fun back(view: View?) {
        finish()
    }

}