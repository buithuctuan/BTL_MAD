package com.example.btl_mad.api

import com.example.btl_mad.data.Fund
import com.example.btl_mad.data.LoginUser
import com.example.btl_mad.data.Question
import com.example.btl_mad.data.VerifyForgotPasswordRequest
import com.example.btl_mad.data.ResetPasswordRequest
import com.example.btl_mad.data.UserRegisterRequest
import com.example.btl_mad.data.VerifyResponse
import com.example.btl_mad.data.statistics.PredictResponse
import com.example.btl_mad.data.statistics.StatisticPieEntry
import com.example.btl_mad.data.statistics.StatisticLineEntry
import com.example.btl_mad.data.statistics.StatisticTotalEntry
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("api/users/register")
    suspend fun registerUser(@Body user: UserRegisterRequest): Response<Map<String, String>>

    @POST("api/users/login")
    suspend fun loginUser(@Body user: LoginUser): Response<Map<String, Any>>

    @GET("api/questions")
    suspend fun getQuestions(): Response<List<Question>>

    @POST("/api/users/verifyForgotPassword")
    suspend fun verifyForgotPassword(@Body request: VerifyForgotPasswordRequest): Response<VerifyResponse>


    @POST("api/users/resetPassword")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Map<String, Any>>

    @GET("/api/statistics/pie")
    suspend fun getPieData(
        @Query("userId") userId: Int,
        @Query("type") type: String,
        @Query("period") period: String
    ): List<StatisticPieEntry>

    @GET("/api/statistics/line")
    suspend fun getLineData(
        @Query("userId") userId: Int,
        @Query("type") type: String,
        @Query("period") period: String
    ): List<StatisticLineEntry>

    @GET("/api/statistics/summary")
    suspend fun getStatisticTotal(
        @Query("userId") userId: Int,
        @Query("type") type: String,
        @Query("period") period: String
    ): StatisticTotalEntry

    @GET("/api/statistics/predict")
    suspend fun getPrediction(
        @Query("userId") userId: Int,
        @Query("type") type: String
    ): PredictResponse

    @GET("/api/transaction_types/{user_id}")
    suspend fun getTransactionTypes(@Path("user_id") userId: Int): List<Fund>

}