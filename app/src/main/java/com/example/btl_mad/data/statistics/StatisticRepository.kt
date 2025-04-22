package com.example.btl_mad.data.statistics

import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.Fund

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

    suspend fun getTransactionTypes(userId: Int): List<Fund> {
        return RetrofitClient.apiService.getTransactionTypes(userId)
    }


}

