package com.example.btl_mad.ui.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.ui.fund.AddFundActivity
import com.example.btl_mad.ui.home.HomeFragment
import com.example.btl_mad.ui.profile.ProfileFragment
import com.example.btl_mad.ui.statistics.StatisticsFragment
import com.example.btl_mad.ui.transaction.AddTransactionExpenseActivity
import com.example.btl_mad.ui.transaction.AddTransactionIncomeActivity
import com.example.btl_mad.ui.transactionhistory.SpendingHistory
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var navHome: LinearLayout
    private lateinit var navTransactions: LinearLayout
    private lateinit var navStatistics: LinearLayout
    private lateinit var navProfile: LinearLayout
    private lateinit var nav_add: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        navHome = findViewById(R.id.nav_home)
        navTransactions = findViewById(R.id.nav_transactions)
        navStatistics = findViewById(R.id.nav_statistics)
        navProfile = findViewById(R.id.nav_profile)
        nav_add = findViewById(R.id.nav_add)

        navigateToHome()

        navHome.setOnClickListener {
            Log.d("NAVIGATION", "Home selected")
            navigateToHome()
        }

        navStatistics.setOnClickListener {
            Log.d("NAVIGATION", "Statistics selected")
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, StatisticsFragment())
                .addToBackStack("StatisticsFragment")
                .commit()
            setActiveTab(navStatistics)
        }


        //Lắng nghe sự kiện click thu chi
        navTransactions.setOnClickListener{
            val intent = Intent(this, SpendingHistory::class.java)
            startActivity(intent)
        }
        // Lắng nghe sk click dâu +
        navProfile.setOnClickListener {
            Log.d("NAVIGATION", "Profile selected")
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, ProfileFragment())
                .addToBackStack("ProfileFragment")
                .commit()
            setActiveTab(navProfile)
        }

        nav_add.setOnClickListener {
            Log.d("NAVIGATION", "Add Transaction clicked")
            val dialog = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.dialog_choose_type, null)
            dialog.setContentView(view)
            dialog.show()

            val btnIncome = view.findViewById<TextView>(R.id.btnIncome)
            val btnExpense = view.findViewById<TextView>(R.id.btnExpense)
            val btnCancel = view.findViewById<TextView>(R.id.btnCancel)

            btnIncome.setOnClickListener {
                Log.d("NAVIGATION", "Add Income")
                startActivity(Intent(this, AddTransactionIncomeActivity::class.java))
                dialog.dismiss()
            }

            btnExpense.setOnClickListener {
                Log.d("NAVIGATION", "Add Expense")
                startActivity(Intent(this, AddTransactionExpenseActivity::class.java))
                dialog.dismiss()
            }

            btnCancel.setOnClickListener {
                Log.d("NAVIGATION", "Cancel Add Dialog")
                dialog.dismiss()
            }
        }
    }

    fun navigateToHome() {
        Log.d("NAVIGATION", "Navigating to HomeFragment")
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, HomeFragment())
            .commit()
        setActiveTab(navHome)
    }

    private fun setActiveTab(selectedNav: LinearLayout) {
        val allNavs = listOf(navHome, navTransactions, navStatistics, navProfile)
        allNavs.forEach { it.isSelected = false }
        selectedNav.isSelected = true
    }

    override fun onBackPressed() {
        val backStackCount = supportFragmentManager.backStackEntryCount
        Log.d("BACKSTACK", "Số Fragment trong stack: $backStackCount")

        if (backStackCount > 0) {
            Log.d("BACKSTACK", "Popping backstack...")
            supportFragmentManager.popBackStack()
        } else {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.content_frame)
            Log.d("BACKSTACK", "Current fragment: ${currentFragment?.javaClass?.simpleName}")

            if (currentFragment is BaseFragment && currentFragment.shouldNavigateToHomeOnBack()) {
                Log.d("BACKSTACK", "Fragment yêu cầu về Home, gọi navigateToHome()")
                navigateToHome()
            } else {
                Log.d("BACKSTACK", "Không còn fragment nào, gọi super.onBackPressed()")
                super.onBackPressed()
            }
        }
    }
}
