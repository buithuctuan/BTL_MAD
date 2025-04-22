package com.example.btl_mad.data.statistics

data class PredictResponse(
    val average: Float,
    val current_spent: Float,
    val predicted: Float,
    val percent_change: Float
)
