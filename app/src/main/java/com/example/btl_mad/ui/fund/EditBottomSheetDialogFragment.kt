package com.example.btl_mad.ui.fund

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.FundResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var iconSelector: LinearLayout
    private lateinit var selectedIcon: ImageView
    private lateinit var fundNameEditText: EditText
    private lateinit var limitEditText: EditText
    private var fundId: Int = -1
    private var userId: Int = -1

    private var icon: String = ""
    private var name: String = ""
    private var budget: Int = 0
    private var pos_icon: Int = 0



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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho BottomSheet
        val view = inflater.inflate(R.layout.dialog_edit, container, false)
        userId = SharedPrefManager.getUserId(requireContext())
        iconSelector = view.findViewById(R.id.iconSelector)
        selectedIcon = view.findViewById(R.id.selectedIcon)

        iconSelector.setOnClickListener {
            showIconDialog()
        }

        // Handle update button
        val updateButton = view.findViewById<AppCompatButton>(R.id.btnUpdate)
        updateButton.setOnClickListener {
            updateFundInfo()
        }

        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // Tìm nút đóng và gán sự kiện
        val closeButton = view.findViewById<ImageView>(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss() // Đóng BottomSheet khi nhấn nút "X"
        }
        arguments?.let {
            fundId = it.getInt("fundId", 0)
            userId = it.getInt("userId", 0)
            name = it.getString("name", "")
            icon = it.getString("icon", "")
            budget = it.getInt("budget", 0)
        }

        iconSelector = view.findViewById(R.id.iconSelector)
        selectedIcon = view.findViewById(R.id.selectedIcon)
        fundNameEditText = view.findViewById(R.id.fundNameEditText)
        limitEditText = view.findViewById(R.id.limitEditText)

        // Set dữ liệu nhận được vào giao diện
        fundNameEditText.setText(name)
        limitEditText.setText(budget.toString())

        // Set icon (Giả sử bạn có các icon tương ứng với các giá trị icon)
        if (icon.isNotEmpty()) {
            // Chuyển icon string thành resource ID nếu bạn lưu icon dưới dạng resource ID (sử dụng icon tương ứng)
            val iconResource = resources.getIdentifier(icon, "drawable", requireContext().packageName)
            selectedIcon.setImageResource(iconResource)
        }

        return view
    }

    private fun updateFundInfo() {
        val updatedName = fundNameEditText.text.toString()
        val updatedBudget = limitEditText.text.toString().toFloatOrNull()

        if (updatedName.isBlank() || updatedBudget == null || updatedBudget <= 0) {
            Toast.makeText(requireContext(), "Thông tin không hợp lệ!", Toast.LENGTH_SHORT).show()
            return
        }

        // Gửi dữ liệu lên API để cập nhật
        updateTransactionTypeInAPI(fundId, updatedName, userId,
            "fund_icon_$pos_icon", updatedBudget)
    }

    private fun updateTransactionTypeInAPI(fundId: Int, name: String, user_id: Int, icon: String, budget: Float) {
        val apiService = RetrofitClient.apiService
        apiService.updateTransactionType(fundId, name, user_id, icon, budget).enqueue(object : Callback<FundResponse> {
            override fun onResponse(call: Call<FundResponse>, response: Response<FundResponse>) {
                println(response)
                if (response.isSuccessful) {
                    val fundResponse = response.body()
                    if (fundResponse != null) {
                        if (fundResponse.status == 200) {
                            Toast.makeText(requireContext(), "Sửa hũ chi tiêu thành công", Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), ListFund::class.java)
                            startActivity(intent)
                            dismiss()
                        } else {
                            // Hiển thị thông báo lỗi
                            Toast.makeText(requireContext(), fundResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    dismiss()  // Đóng BottomSheet sau khi thành công
                } else {
                    Toast.makeText(requireContext(), "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FundResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmationDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.delete_confirm, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val btnYes = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnNo = dialogView.findViewById<Button>(R.id.btnCancle)

        btnYes.setOnClickListener {
            deleteTransactionType()
            dialog.dismiss()
        }

        // If user clicks No, dismiss the dialog
        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteTransactionType() {
        val call = RetrofitClient.apiService.deleteTransactionType(fundId)

        call.enqueue(object : Callback<FundResponse> {
            override fun onResponse(call: Call<FundResponse>, response: Response<FundResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Xóa hũ chi tiêu thành công", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), ListFund::class.java)
                    startActivity(intent)
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Lỗi khi xóa", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<FundResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showIconDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_icon_grid, null)
        val gridView = dialogView.findViewById<GridView>(R.id.gridViewIcons)

        val adapter = object : BaseAdapter() {
            override fun getCount() = iconList.size
            override fun getItem(position: Int) = iconList[position]
            override fun getItemId(position: Int) = position.toLong()
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_icon, parent, false)
                val image = view.findViewById<ImageView>(R.id.icon)
                image.setImageResource(iconList[position])
                return view
            }
        }

        gridView.adapter = adapter

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        gridView.setOnItemClickListener { _, _, position, _ ->
            selectedIcon.setImageResource(iconList[position])
            pos_icon = position + 1
            dialog.dismiss()
        }

        dialog.show()
    }

}

