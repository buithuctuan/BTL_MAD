package com.example.btl_mad.ui.home.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R

// Data class
data class SpendingLimitItem(val category: String, val limit: Int, val spent: Int)

class SpendingLimitAdapter(private val data: List<SpendingLimitItem>) :
    RecyclerView.Adapter<SpendingLimitAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory = view.findViewById<TextView>(R.id.tvCategory)
        val tvLimit = view.findViewById<TextView>(R.id.tvLimit)
        val tvSpent = view.findViewById<TextView>(R.id.tvSpent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_spending_limit, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.tvCategory.text = item.category
        holder.tvLimit.text = "%,d đ".format(item.limit)
        holder.tvSpent.text = "%,d đ".format(item.spent)

        if (item.spent > item.limit) {
            holder.tvSpent.setTextColor(Color.RED)
        } else {
            holder.tvSpent.setTextColor(Color.BLACK)
        }
    }
}
