package com.example.btl_mad.data.statistics

data class StatisticLineEntry(
    val timeUnit: String,      // Ví dụ: "Thứ 2", "01", "10h",...
    val amount: Float,         // Giá trị tại thời điểm đó
    val period: String         // "Tuần này" hoặc "Tuần trước" để phân biệt series
)
