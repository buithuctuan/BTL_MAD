package com.example.btl_mad.api

import com.example.btl_mad.data.CategorySpending
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
import com.example.btl_mad.data.ChangePasswordRequest
import com.example.btl_mad.data.ExpenseRequest
import com.example.btl_mad.data.ExpenseResponse
import com.example.btl_mad.data.FundResponse
import com.example.btl_mad.data.Funds_home
import com.example.btl_mad.data.SpendingSummaryResponse
import com.example.btl_mad.data.TotalSpendingAndIncomeResponse
import com.example.btl_mad.data.Transaction
import com.example.btl_mad.data.TransactionRequest
import com.example.btl_mad.data.Transaction_home
import com.example.btl_mad.data.Notification
import com.example.btl_mad.data.TransactionType
import com.example.btl_mad.data.TransFund
import com.example.btl_mad.data.FundInfo
import com.example.btl_mad.data.FundDetail
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("api/users/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Map<String, Any>>


    // Get notifications
    @GET("/api/notifications/")
    fun getNotifications(
        @Query("user_id") userId: Int
    ): Call<List<Notification>>

    @POST("/api/notifications/")
    fun addNotification(
        @Query("user_id") name: Int,
        @Query("title") title: String,
        @Query("content") content: String
    ): Call<FundResponse>

    // GET transaction types by Path (trả về Fund)
    @GET("/api/transaction-type/get-list-transaction-type")
    fun getTransactionTypes(
        @Query("user_id") userId: Int
    ): Call<List<Fund>>

    // GET transaction types by Query (trả về TransactionType)
    @GET("api/transaction_types")
    fun getTransactionTypesByQuery(
        @Query("user_id") userId: Int
    ): Call<List<TransactionType>>

    // Giao dịch quỹ
    @GET("/api/transaction-type/get-list-transaction")
    fun getTransactionsFund(
        @Query("fund_id") fundId: Int,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("type") type: String,
        @Query("search") search: String?
    ): Call<List<TransFund>>

    @GET("/api/transaction-type/get-detail-transaction-type")
    fun getFundInfo(
        @Query("fund_id") fundId: Int,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Call<List<FundInfo>>

    @POST("/api/transaction-type/create")
    fun addFund(
        @Query("name") name: String,
        @Query("user_id") userId: Int,
        @Query("icon") icon: String,
        @Query("budget") budget: Float
    ): Call<FundResponse>

    @PUT("/api/transaction-type/update")
    fun updateTransactionType(
        @Query("id") fundId: Int,
        @Query("name") name: String,
        @Query("user_id") userId: Int,
        @Query("icon") icon: String,
        @Query("budget") budget: Float
    ): Call<FundResponse>

    @DELETE("/api/transaction-type/delete")
    fun deleteTransactionType(
        @Query("id") Id: Int
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

    @GET("/api/funds_home")
    suspend fun getFundsByUserId(@Query("user_id") userId: Int): List<Funds_home>

    @GET("/api/statistics/by-category")
    suspend fun getSpendingByCategory(@Query("user_id") userId: Int): List<CategorySpending>

    @GET("/api/transactions/recent")
    suspend fun getRecentTransactions(
        @Query("user_id") userId: Int,
        @Query("type") type: String,
        @Query("limit") limit: Int = 5
    ): List<Transaction_home>

    @GET("/api/spending_summary_home")
    suspend fun getSpendingSummary(@Query("user_id") userId: Int): SpendingSummaryResponse

    @POST("api/getListTransactions")
    fun getListTransactions(
        @Body request: TransactionRequest
    ): Call<List<Transaction>>
    // lấy tổng số tiền chi và thu trong một khoảng thời gian
    @GET("/api/getTotalSpendingAndIncome/{user_id}")
    fun getTotalSpendingAndIncome(
        @Path("user_id") userId: Int,
        @Query("filter_date") filterDate: String
    ): Call<TotalSpendingAndIncomeResponse>
    // xoa giao dich
    @DELETE("/api/deleteTransaction/{id}")
    fun deleteTransaction(
        @Path("id") Id: Int
    ): Call<FundResponse>

    // sua giao dich
    @POST("/api/modifyTransaction")
    fun updateTransaction(
        @Body request: Transaction
    ): Call<FundResponse>

    @POST("/api/users/update-profile")
    suspend fun updateProfile(@Body data: Map<String, String>): Response<ResponseBody>

    @GET("/api/statistics/pie/custom")
    suspend fun getPieDataCustom(
        @Query("userId") userId: Int,
        @Query("type") type: String,
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): List<StatisticPieEntry>

    @GET("/api/statistics/line/custom")
    suspend fun getLineDataCustom(
        @Query("userId") userId: Int,
        @Query("type") type: String,
        @Query("from") fromDate: String,
        @Query("to") toDate: String
    ): List<StatisticLineEntry>
    @GET("api/statistics/summary/custom")
    suspend fun getStatisticTotalCustom(
        @Query("userId") userId: Int,
        @Query("type") type: String,
        @Query("from_date") fromDate: String,
        @Query("to_date") toDate: String
    ): StatisticTotalEntry

}

