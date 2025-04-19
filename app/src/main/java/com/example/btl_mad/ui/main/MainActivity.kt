package com.example.btl_mad.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.btl_mad.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo BottomNavigationView
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment(), false) // Không thêm vào back stack
                    true
                }
                R.id.nav_transactions -> {
                    loadFragment(TransactionsFragment(), true)
                    true
                }
                R.id.nav_statistics -> {
                    loadFragment(StatisticsFragment(), true)
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment(), true)
                    true
                }
                else -> false
            }
        }

        // Mặc định hiển thị HomeFragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), false)
            bottomNavigation.selectedItemId = R.id.nav_home
        }
    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commit()
    }

    override fun onBackPressed() {
        // Nếu đang ở HomeFragment, thoát ứng dụng
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is HomeFragment || supportFragmentManager.backStackEntryCount == 0) {
            super.onBackPressed() // Thoát ứng dụng
        } else {
            // Quay lại HomeFragment
            loadFragment(HomeFragment(), false)
            bottomNavigation.selectedItemId = R.id.nav_home
            supportFragmentManager.popBackStack() // Xóa back stack
        }
    }
}