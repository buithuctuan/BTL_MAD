package com.example.btl_mad.ui.transaction

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.btl_mad.R

class CalendarAdapter(
    private val context: Context,
    private val days: List<Int>,
    private var selectedDay: Int,
    private val month: Int,
    private val year: Int,
    private val onDateClick: (Int) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): Any = days[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.calendar_day_item, parent, false)
        val dayText = view as TextView
        val day = days[position]

        if (day == 0) {
            dayText.text = ""
            dayText.isEnabled = false
            dayText.isClickable = false
        } else {
            dayText.text = day.toString()
            dayText.isEnabled = true
            dayText.isClickable = true

            // Set background khi được chọn
            if (day == selectedDay) {
                dayText.setBackgroundResource(R.drawable.calendar_day_selected_background)
            } else {
                dayText.setBackgroundResource(R.drawable.calendar_day_background)
            }

            dayText.setOnClickListener {
                selectedDay = day // cập nhật ngày được chọn
                notifyDataSetChanged() // refresh lại toàn bộ lưới
                onDateClick(day) // gọi callback
            }
        }

        return view
    }
}
