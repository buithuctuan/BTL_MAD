package com.example.btl_mad.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.example.btl_mad.R
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.ui.login.LoginActivity
import com.example.btl_mad.utils.DialogUtils
import com.example.btl_mad.utils.SharedPrefManager

class SettingsFragment : BaseFragment() {

    override fun getLayoutId(): Int = R.layout.fragment_settings
    override fun getToolbarTitle(): String? = "Cài đặt"
    override fun useToolbar(): Boolean = true
    override fun showBackButton(): Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val switchNotify = view.findViewById<SwitchCompat>(R.id.switchNotify)
        val btnReset = view.findViewById<Button>(R.id.btnResetData)

        switchNotify.isChecked = SharedPrefManager.isNotificationEnabled(requireContext())

        switchNotify.setOnCheckedChangeListener { _, isChecked ->
            SharedPrefManager.setNotificationEnabled(requireContext(), isChecked)
            Toast.makeText(
                context,
                "Thông báo ${if (isChecked) "bật" else "tắt"}",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnReset.setOnClickListener {
            DialogUtils.showWarningDialog(
                context = requireContext(),
                message = "Bạn có chắc chắn muốn xóa toàn bộ dữ liệu và đăng xuất?",
                onConfirm = {
                    SharedPrefManager.clearAll(requireContext())
                    DialogUtils.showSuccessDialog(
                        context = requireContext(),
                        message = "Đã xóa toàn bộ dữ liệu\nvà đăng xuất khỏi hệ thống",
                        showCreateNew = false,
                        onBack = {
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finish()
                        }
                    )
                }
            )
        }
    }
}
