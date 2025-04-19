package com.example.btl_mad.ui.fund

import android.app.AlertDialog
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
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R


class AddFundActivity : AppCompatActivity() {

    private lateinit var iconSelector: LinearLayout
    private lateinit var selectedIcon: ImageView

    private val iconList = listOf(
        R.drawable.fund_icon_1,
        R.drawable.fund_icon_2,
        R.drawable.fund_icon_3,
        R.drawable.fund_icon_4,
        R.drawable.fund_icon_5,
        R.drawable.fund_icon_6,
        R.drawable.fund_icon_6,
        R.drawable.fund_icon_6
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
            showSuccess()
        }

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
            dialog.dismiss()
        }

        dialog.show()
    }


}
