package com.example.btl_mad.ui.home.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.data.Funds_home
import java.text.DecimalFormat

class FundsLimitAdapter(
    private val context: Context,
    private val items: List<Funds_home>
) : RecyclerView.Adapter<FundsLimitAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvLimit: TextView = view.findViewById(R.id.tvLimit)
        val tvSpent: TextView = view.findViewById(R.id.tvSpent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_fund_limit, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val formatter = DecimalFormat("#,###")

        holder.tvName.text = item.name
        holder.tvLimit.text = formatter.format(item.spending_limit) + " đ"
        holder.tvSpent.text = formatter.format(item.total_expenses) + " đ"

        // Load icon theo tên từ DB (icon = "ic_essential" → R.drawable.ic_essential)
        val iconResId = context.resources.getIdentifier(item.icon, "drawable", context.packageName)
        if (iconResId != 0) {
            holder.imgIcon.setImageResource(iconResId)
        } else {
            holder.imgIcon.setImageResource(R.drawable.ic_huong_thu)
        }

        // Màu đỏ nếu vượt hạn mức
        holder.tvSpent.setTextColor(
            if (item.total_expenses > item.spending_limit)
                Color.RED
            else
                ContextCompat.getColor(context, R.color.green)
        )
    }
}
