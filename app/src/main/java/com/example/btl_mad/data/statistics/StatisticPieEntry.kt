package com.example.btl_mad.data.statistics

data class StatisticPieEntry(
    val category: String,   // Ví dụ: "Ăn uống", "Lương"
    val percent: Float,
    val amount: Int
)
