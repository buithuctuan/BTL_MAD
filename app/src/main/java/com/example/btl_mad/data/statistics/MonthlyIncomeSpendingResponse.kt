package com.example.btl_mad.data.statistics

data class MonthlyIncomeSpendingResponse(
    val income: List<StatisticLineEntry>,
    val spending: List<StatisticLineEntry>
)
