package com.example.btl_mad.data.statistics

import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.Fund
import com.example.btl_mad.data.Funds_home
import com.example.btl_mad.data.Transaction_home
import retrofit2.Call
import retrofit2.http.Query

class StatisticRepository {
    suspend fun getPieData(userId: Int, type: String, period: String): List<StatisticPieEntry> {
        return RetrofitClient.apiService.getPieData(userId, type, period)
    }

    suspend fun getLineData(userId: Int, type: String, period: String): List<StatisticLineEntry> {
        return RetrofitClient.apiService.getLineData(userId, type, period)
    }

    suspend fun getTotal(userId: Int, type: String, period: String): StatisticTotalEntry {
        return RetrofitClient.apiService.getStatisticTotal(userId, type, period)
    }

    suspend fun getPredictedSpending(userId: Int, type: String): PredictResponse {
        return RetrofitClient.apiService.getPrediction(userId, type)
    }

    suspend fun getFunds(userId: Int): List<Funds_home> {
        return RetrofitClient.apiService.getFundsByUserId(userId)
    }
    suspend fun getRecentTransactions(userId: Int, type: String): List<Transaction_home> {
        return RetrofitClient.apiService.getRecentTransactions(
            userId = userId,
            type = type,
            limit = 5
        )
    }
    // Thống kê biểu đồ tròn theo khoảng tùy chọn
    suspend fun getPieDataCustom(userId: Int, type: String, fromDate: String, toDate: String): List<StatisticPieEntry> {
        return RetrofitClient.apiService.getPieDataCustom(userId, type, fromDate, toDate)
    }

    // Thống kê biểu đồ đường theo khoảng tùy chọn
    suspend fun getLineDataCustom(userId: Int, type: String, fromDate: String, toDate: String): List<StatisticLineEntry> {
        return RetrofitClient.apiService.getLineDataCustom(userId, type, fromDate, toDate)
    }
    suspend fun getTotalCustom(userId: Int, type: String, fromDate: String, toDate: String): StatisticTotalEntry {
        return RetrofitClient.apiService.getStatisticTotalCustom(userId, type, fromDate, toDate)
    }

    suspend fun getMonthlyIncomeVsSpending(userId: Int): Pair<List<StatisticLineEntry>, List<StatisticLineEntry>> {
        val response = RetrofitClient.apiService.getMonthlyIncomeVsSpending(userId)
        return response.income to response.spending
    }

}

