package com.example.btl_mad.api

import com.example.btl_mad.data.Fund
import com.example.btl_mad.data.FundDetail
import com.example.btl_mad.data.FundInfo
import com.example.btl_mad.data.FundResponse
import com.example.btl_mad.data.LoginUser
import com.example.btl_mad.data.Notification
import com.example.btl_mad.data.Question
import com.example.btl_mad.data.User
import com.example.btl_mad.data.VerifyForgotPasswordRequest
import com.example.btl_mad.data.ResetPasswordRequest
import com.example.btl_mad.data.TransFund
import com.example.btl_mad.data.VerifyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    @POST("api/users/register")
    suspend fun registerUser(@Body user: User): Response<Map<String, String>>

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

    // API GET để lấy transaction types với user_id và từ khóa tìm kiếm
    @GET("/api/transaction_types/{user_id}")
    fun getTransactionTypes(
        @Path("user_id") userId: Int): Call<List<Fund>>

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

}