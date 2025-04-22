package com.example.btl_mad.ui.profile

import android.view.View
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.ChangePasswordRequest
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.utils.DialogUtils
import com.example.btl_mad.utils.SharedPrefManager
import kotlinx.coroutines.launch

class ChangePasswordFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_change_password
    override fun getToolbarTitle(): String? = "Đổi mật khẩu"
    override fun useToolbar() = true
    override fun showBackButton() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val edtOld = view.findViewById<EditText>(R.id.edtOldPassword)
        val edtNew = view.findViewById<EditText>(R.id.edtNewPassword)
        val edtConfirm = view.findViewById<EditText>(R.id.edtConfirmPassword)
        val btnSave = view.findViewById<Button>(R.id.btnSavePassword)

        btnSave.setOnClickListener {
            val oldPassword = edtOld.text.toString()
            val newPassword = edtNew.text.toString()
            val confirmPassword = edtConfirm.text.toString()

            if (newPassword != confirmPassword) {
                DialogUtils.showErrorDialog(requireContext(), "Mật khẩu xác nhận không khớp!")
                return@setOnClickListener
            }

            DialogUtils.showWarningDialog(
                context = requireContext(),
                message = "Bạn có chắc chắn muốn đổi mật khẩu?",
                confirmText = "Đồng ý",
                cancelText = "Hủy",
                onConfirm = {
                    val userId = SharedPrefManager.getUserId(requireContext())
                    val request = ChangePasswordRequest(
                        id = userId,
                        old_password = oldPassword,
                        new_password = newPassword
                    )

                    lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.apiService.changePassword(request)
                            if (response.isSuccessful && response.body()?.get("status") == true) {
                                DialogUtils.showSuccessDialog(
                                    context = requireContext(),
                                    message = "Đổi mật khẩu thành công!",
                                    onBack = {
                                        edtOld.text.clear()
                                        edtNew.text.clear()
                                        edtConfirm.text.clear()
                                    }
                                )
                            } else {
                                val errorMsg = response.body()?.get("message") ?: "Đổi mật khẩu thất bại."
                                DialogUtils.showErrorDialog(requireContext(), errorMsg.toString())
                            }
                        } catch (e: Exception) {
                            DialogUtils.showErrorDialog(requireContext(), "Lỗi kết nối đến máy chủ.")
                        }
                    }
                }
            )
        }
    }
}