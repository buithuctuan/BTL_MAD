package com.example.btl_mad.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.ui.main.MainActivity
import com.example.btl_mad.databinding.ActivityLoginBinding

// LoginActivity: Màn hình đăng nhập, cho phép người dùng đăng nhập bằng Facebook hoặc số điện thoại.
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Xử lý sự kiện nhấn nút "Đăng nhập với Facebook"
        binding.ivFaceLogin.setOnClickListener {
            // TODO: Tích hợp đăng nhập với Facebook (sử dụng Facebook SDK)
            navigateToMainActivity()
        }

        // Xử lý sự kiện nhấn nút "Đăng nhập với số điện thoại"
        binding.ivPhoneLogin.setOnClickListener {
            // TODO: Chuyển đến màn hình nhập số điện thoại
            navigateToMainActivity()
        }
    }

    // Hàm chuyển đến MainActivity sau khi đăng nhập thành công
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Đóng LoginActivity để không quay lại
    }
}