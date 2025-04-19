package com.example.btl_mad.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.LoginUser
import com.example.btl_mad.ui.forgotpassword.ForgotPasswordActivity
import com.example.btl_mad.ui.main.MainActivity
import com.example.btl_mad.ui.signup.SignUpActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.google.gson.Gson

class LoginActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var ivTogglePassword: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Xử lý hiện/ẩn mật khẩu
        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
            }
            etPassword.setSelection(etPassword.text.length)
        }

        // Xử lý nút Đăng nhập
        findViewById<View>(R.id.btnLogin).setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo đối tượng LoginUser
            val loginUser = LoginUser(username = username, pass_ = password)

            // Gọi API đăng nhập
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.loginUser(loginUser)
                    }
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        // Kiểm tra và ép kiểu message thành String
                        val message = responseBody?.get("message") as? String ?: "Đăng nhập thành công"
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()

                        // Lưu thông tin người dùng vào SharedPreferences
                        val user = responseBody?.get("user") as? Map<*, *>
                        if (user != null) {
                            sharedPreferences.edit()
                                .putString("user", Gson().toJson(user))
                                .apply()
                        }

                        // Chuyển đến MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Đăng nhập thất bại"
                        Toast.makeText(this@LoginActivity, errorBody, Toast.LENGTH_LONG).show()
                    }
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string() ?: "Lỗi server"
                    Toast.makeText(this@LoginActivity, errorBody, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@LoginActivity, "Lỗi kết nối: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Xử lý "Quên mật khẩu"
        findViewById<View>(R.id.tvForgotPassword)?.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // Xử lý "Đăng ký"
        findViewById<View>(R.id.tvSignUp)?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}