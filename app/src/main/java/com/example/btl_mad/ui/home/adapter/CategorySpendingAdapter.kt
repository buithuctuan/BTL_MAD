package com.example.btl_mad.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.data.CategorySpending
import java.text.DecimalFormat

class CategorySpendingAdapter(
    private val items: List<CategorySpending>,
    private val onAddClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ADD = 0
    private val TYPE_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_ADD else TYPE_ITEM
    }

    override fun getItemCount(): Int = items.size + 1  // thêm 1 ô "+"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_ADD) {
            val view = inflater.inflate(R.layout.item_add_category, parent, false)
            object : RecyclerView.ViewHolder(view) {}
        } else {
            val view = inflater.inflate(R.layout.item_category_spending, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_ADD) {
            holder.itemView.setOnClickListener {
                onAddClicked()
            }
        } else {
            val item = items[position - 1]
            val viewHolder = holder as ItemViewHolder
            viewHolder.tvCategory.text = item.category
            viewHolder.tvSpent.text = "${DecimalFormat("#,###").format(item.total_spent)} đ"

            val colors = listOf(
                R.color.tu_thien,
                R.color.can_thiet,
                R.color.huong_thu,
                R.color.dao_tao
            )
            val cardView = viewHolder.itemView as CardView
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, colors[position % colors.size])
            )
        }
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvSpent: TextView = view.findViewById(R.id.tvSpent)
    }
}
