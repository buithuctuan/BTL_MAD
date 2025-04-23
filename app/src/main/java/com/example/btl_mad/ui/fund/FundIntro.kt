package com.example.btl_mad.ui.fund

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.btl_mad.R
import com.example.btl_mad.ui.home.HomeFragment
import com.example.btl_mad.ui.main.MainActivity

class FundIntro : AppCompatActivity() {
    private lateinit var navHome: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.fund_intro)

        navHome = findViewById(R.id.nav_home)
        navHome.setOnClickListener {
            navigateToHome()
        }

    }
    fun navigateToHome() {
        val intent = Intent(this@FundIntro, MainActivity::class.java)
        startActivity(intent)
    }

    fun goListFund(view: View?) {
        val intent = Intent(this, ListFund::class.java)
        startActivity(intent)
        finish()
    }

    fun back(view: View) {
        finish()
    }
}