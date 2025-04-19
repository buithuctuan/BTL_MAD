package com.example.btl_mad.ui.resetpassword

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.ResetPasswordRequest
import com.example.btl_mad.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnResetPassword: Button
    private var email: String? = null
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Khởi tạo các view
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)

        // Lấy dữ liệu từ Intent
        email = intent.getStringExtra("email")
        phoneNumber = intent.getStringExtra("phoneNumber")

        // Xử lý nút Đặt lại mật khẩu
        btnResetPassword.setOnClickListener {
            val newPassword = etNewPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validate đầu vào
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gửi yêu cầu đặt lại mật khẩu đến API
            resetPassword(newPassword)
        }
    }

    private fun resetPassword(newPassword: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Tạo đối tượng ResetPasswordRequest
                val request = ResetPasswordRequest(
                    email = email ?: "",
                    phoneNumber = phoneNumber ?: "",
                    newPassword = newPassword
                )

                // Gửi yêu cầu đặt lại mật khẩu đến API
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.resetPassword(request)
                }

                if (response.isSuccessful) {
                    Toast.makeText(this@ResetPasswordActivity, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Đặt lại mật khẩu thất bại: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(this@ResetPasswordActivity, "Lỗi server: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ResetPasswordActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}