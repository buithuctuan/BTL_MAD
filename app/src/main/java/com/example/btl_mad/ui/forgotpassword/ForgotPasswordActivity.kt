package com.example.btl_mad.ui.forgotpassword

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.Question
import com.example.btl_mad.data.VerifyForgotPasswordRequest
import com.example.btl_mad.ui.resetpassword.ResetPasswordActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var etPhoneNumber: EditText
    private lateinit var spinnerSecurityQuestion: Spinner
    private lateinit var etSecurityAnswer: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnLogin: Button
    private lateinit var sharedPreferences: SharedPreferences
    private var questions: List<Question> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pass)

        // Khởi tạo các view
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        spinnerSecurityQuestion = findViewById(R.id.spinnerSecurityQuestion)
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer)
        etEmail = findViewById(R.id.etEmail)
        btnLogin = findViewById(R.id.btnLogin)

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("SecurityQuestions", MODE_PRIVATE)

        // Tải danh sách câu hỏi bảo mật
        loadSecurityQuestions()

        // Xử lý nút Xác nhận
        btnLogin.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val securityQuestionPosition = spinnerSecurityQuestion.selectedItemPosition
            val securityAnswer = etSecurityAnswer.text.toString().trim()
            val email = etEmail.text.toString().trim()

            // Validate đầu vào
            if (phoneNumber.isEmpty() || email.isEmpty() || securityQuestionPosition == 0 || securityAnswer.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidVietnamPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gửi yêu cầu xác minh
            verifyForgotPassword(phoneNumber, securityAnswer, email, securityQuestionPosition)
        }
    }

    private fun loadSecurityQuestions() {
        // Kiểm tra cache
        val cachedQuestions = sharedPreferences.getString("questions", null)
        if (cachedQuestions != null) {
            val type = object : TypeToken<List<Question>>() {}.type
            questions = Gson().fromJson(cachedQuestions, type)
            setupSpinner()
            return
        }

        // Tải từ API
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getQuestions()
                }
                if (response.isSuccessful) {
                    questions = response.body() ?: emptyList()
                    // Lưu vào cache
                    sharedPreferences.edit().putString("questions", Gson().toJson(questions)).apply()
                    setupSpinner()
                } else {
                    Toast.makeText(this@ForgotPasswordActivity, "Không thể tải câu hỏi: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ForgotPasswordActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinner() {
        val questionDetails = mutableListOf("Chọn câu hỏi bảo mật")
        questionDetails.addAll(questions.map { it.question_detail })
        val adapter = ArrayAdapter(this@ForgotPasswordActivity, android.R.layout.simple_spinner_item, questionDetails)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSecurityQuestion.adapter = adapter
    }

    private fun verifyForgotPassword(phone: String, securityAnswer: String, email: String, questionPosition: Int) {
        if (questionPosition <= 0) {
            Toast.makeText(this, "Vui lòng chọn câu hỏi bảo mật", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val questionId = questions[questionPosition - 1].id
                val request = VerifyForgotPasswordRequest(
                    phoneNumber = phone,
                    email = email,
                    question_id = questionId,
                    answer_for_forgot_password = securityAnswer
                )

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.verifyForgotPassword(request)
                }

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val message = responseBody?.message ?: "Xác minh thành công"

                    if (responseBody?.status == true) {
                        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("phoneNumber", phone)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Xác minh thất bại"
                    Toast.makeText(this@ForgotPasswordActivity, errorBody, Toast.LENGTH_LONG).show()
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "Lỗi server"
                Toast.makeText(this@ForgotPasswordActivity, errorBody, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@ForgotPasswordActivity, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun isValidVietnamPhoneNumber(phoneNumber: String): Boolean {
        val vietnamPhonePattern = "^(03|05|07|08|09)[0-9]{8}$"
        return phoneNumber.matches(vietnamPhonePattern.toRegex())
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailPattern.toRegex())
    }
}