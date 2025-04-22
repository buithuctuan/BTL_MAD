package com.example.btl_mad.api

import com.example.btl_mad.data.*
import retrofit2.Response
import retrofit2.Call
import retrofit2.http.*

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
    @POST("api/getListTransactions")
    fun getListTransactions(
        @Body request: TransactionRequest): Call<List<Transaction>>
}
