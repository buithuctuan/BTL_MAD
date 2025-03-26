package com.example.btl_mad.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.ui.main.MainActivity
import com.example.btl_mad.ui.onboarding.OnboardingActivity
// SplashActivity: Màn hình khởi động của ứng dụng, hiển thị trong 2 giây trước khi chuyển sang OnboardingActivity.
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Chuyển sang MainActivity sau 2 giây
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish() // Đóng SplashActivity
        }, 2000) // Thời gian hiển thị: 2000ms = 2 giây
    }
}