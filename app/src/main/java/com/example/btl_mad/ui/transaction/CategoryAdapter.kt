package com.example.btl_mad.ui.transaction

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.btl_mad.R
import com.example.btl_mad.data.TransactionType

class CategoryAdapter(
    context: Context,
    private val categories: List<TransactionType>
) : ArrayAdapter<TransactionType>(context, 0, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)

        val category = getItem(position)
        val iconView = view.findViewById<ImageView>(R.id.categoryIcon)
        val nameView = view.findViewById<TextView>(R.id.categoryName)

        // Hiển thị tên
        nameView.text = category?.name

        // Hiển thị icon (giả định icon là tên tài nguyên trong drawable)
        try {
            val resId = context.resources.getIdentifier(category?.icon ?: "ic_default_category", "drawable", context.packageName)
            iconView.setImageResource(resId)
        } catch (e: Exception) {
            iconView.setImageResource(R.drawable.ic_huong_thu) // Biểu tượng mặc định nếu lỗi
        }

        return view
    }
}