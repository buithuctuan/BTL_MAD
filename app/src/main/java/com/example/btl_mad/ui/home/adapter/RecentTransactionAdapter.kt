package com.example.btl_mad.ui.home.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.data.Transaction_home
import java.text.DecimalFormat

class RecentTransactionAdapter(private val items: List<Transaction_home>) :
    RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvNote: TextView = view.findViewById(R.id.tvNote)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val viewDot: View = view.findViewById(R.id.viewDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_transaction_home, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.category
        holder.tvNote.text = item.note
        holder.tvDate.text = item.dot
        holder.tvAmount.text = "${DecimalFormat("#,###").format(item.amount)} đ"

        // Random màu
        val colors = listOf(
            R.color.huong_thu,
            R.color.can_thiet,
            R.color.tu_thien,
            R.color.blue,
            R.color.dao_tao
        )
        val color = ContextCompat.getColor(holder.itemView.context, colors[position % colors.size])
        (holder.viewDot.background as GradientDrawable).setColor(color)
    }
}
