package com.example.btl_mad.ui.transaction

import android.app.Activity
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import com.example.btl_mad.R
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomDatePickerHelper(
    private val activity: Activity,
    private val onDateSelected: (String) -> Unit
) {
    fun show() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_date_picker_dialog, null)
        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val monthYearText: TextView = dialogView.findViewById(R.id.monthYearText)
        val prevMonth: ImageView = dialogView.findViewById(R.id.prevMonth)
        val nextMonth: ImageView = dialogView.findViewById(R.id.nextMonth)
        val calendarGrid: GridView = dialogView.findViewById(R.id.calendarGrid)

        val calendar = Calendar.getInstance()
        var selectedYear = calendar.get(Calendar.YEAR)
        var selectedMonth = calendar.get(Calendar.MONTH)
        var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val displayFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale("vi", "VN"))

        fun updateCalendar() {
            calendar.set(selectedYear, selectedMonth, 1)
            val firstDayOfMonth = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

            val days = mutableListOf<Int>()
            for (i in 0 until firstDayOfMonth) days.add(0)
            for (i in 1..daysInMonth) days.add(i)

            val adapter = CalendarAdapter(activity, days, selectedDay, selectedMonth + 1, selectedYear) { day ->
                selectedDay = day
                calendar.set(selectedYear, selectedMonth, day)
                val formatted = displayFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
                onDateSelected(formatted)
                dialog.dismiss()
            }
            calendarGrid.adapter = adapter
        }

        updateCalendar()

        prevMonth.setOnClickListener {
            selectedMonth--
            if (selectedMonth < 0) {
                selectedMonth = 11
                selectedYear--
            }
            updateCalendar()
            monthYearText.text = "Tháng ${selectedMonth + 1} $selectedYear"
        }

        nextMonth.setOnClickListener {
            selectedMonth++
            if (selectedMonth > 11) {
                selectedMonth = 0
                selectedYear++
            }
            updateCalendar()
            monthYearText.text = "Tháng ${selectedMonth + 1} $selectedYear"
        }

        calendarGrid.setOnItemClickListener { _, _, position, _ ->
            val day = (calendarGrid.adapter as CalendarAdapter).getItem(position) as Int
            if (day != 0) {
                selectedDay = day
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val selectedDate = dateFormat.format(calendar.time)
                onDateSelected(selectedDate)
                dialog.dismiss()
            }
        }

        dialog.show()
        dialog.window?.apply {
            val params = attributes
            params.gravity = Gravity.BOTTOM
            params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT
            attributes = params
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}