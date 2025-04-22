package com.example.btl_mad.api


import com.example.btl_mad.data.*
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.*

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


    // Get notifications
    @GET("/api/notifications/{user_id}")
    fun getNotifications(
        @Path("user_id") userId: Int
    ): Call<List<Notification>>

    // GET transaction types by Path (trả về Fund)
    @GET("/api/transaction_types/{user_id}")
    fun getTransactionTypes(
        @Path("user_id") userId: Int
    ): Call<List<Fund>>

    // GET transaction types by Query (trả về TransactionType)
    @GET("api/transaction_types")
    fun getTransactionTypesByQuery(
        @Query("user_id") userId: Int
    ): Call<List<TransactionType>>

    // Giao dịch quỹ
    @GET("/api/transactions-fund")
    fun getTransactionsFund(
        @Query("fund_id") fundId: Int,
        @Query("user_id") userId: Int,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("type") type: String,
        @Query("search") search: String?
    ): Call<List<TransFund>>

    @GET("/api/fund-info")
    fun getFundInfo(
        @Query("fund_id") fundId: Int,
        @Query("user_id") userId: Int,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Call<List<FundInfo>>

    @GET("/api/fund-detail")
    fun getFundDetail(
        @Query("fund_id") fundId: Int
    ): Call<List<FundDetail>>

    @POST("/api/transaction-type")
    fun addFund(
        @Query("name") name: String,
        @Query("user_id") userId: Int,
        @Query("icon") icon: Int,
        @Query("budget") budget: Float
    ): Call<FundResponse>

    @PUT("/api/transaction-type")
    fun updateTransactionType(
        @Query("id") fundId: Int,
        @Query("name") name: String,
        @Query("user_id") userId: Int,
        @Query("icon") icon: String,
        @Query("budget") budget: Float
    ): Call<FundResponse>

    @DELETE("/api/transaction-type/{id}")
    fun deleteTransactionType(
        @Path("id") Id: Int
    ): Call<FundResponse>

    // Thêm chi tiêu
    @POST("api/expense")
    fun saveExpense(@Body request: ExpenseRequest): Call<ExpenseResponse>

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

}

