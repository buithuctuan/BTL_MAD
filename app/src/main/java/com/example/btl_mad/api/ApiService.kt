package com.example.btl_mad.api

import com.example.btl_mad.data.LoginUser
import com.example.btl_mad.data.Question
import com.example.btl_mad.data.User
import com.example.btl_mad.data.VerifyForgotPasswordRequest
import com.example.btl_mad.data.ResetPasswordRequest
import com.example.btl_mad.data.VerifyResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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
}