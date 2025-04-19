package com.example.btl_mad.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.ui.login.LoginActivity
import com.example.btl_mad.ui.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        android.util.Log.d("SplashActivity", "Starting delay")

        // Kiểm tra xem người dùng đã xem onboarding chưa
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val hasSeenOnboarding = sharedPreferences.getBoolean("hasSeenOnboarding", false)

        // Tự động chuyển sau 2 giây
        Handler(Looper.getMainLooper()).postDelayed({
            if (hasSeenOnboarding) {
                android.util.Log.d("SplashActivity", "Navigating to LoginActivity")
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                android.util.Log.d("SplashActivity", "Navigating to OnboardingActivity")
                startActivity(Intent(this, OnboardingActivity::class.java))
            }
            finish()
        }, 2000)
    }
}