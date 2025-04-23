package com.example.btl_mad.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.utils.DialogUtils
import com.example.btl_mad.utils.SharedPrefManager
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.InputStream

class ProfileInfoFragment : BaseFragment() {

    private val PICK_IMAGE_REQUEST = 100
    private var avatarUri: Uri? = null
    private lateinit var imgAvatar: ImageView

    override fun getLayoutId() = R.layout.fragment_profile_info
    override fun getToolbarTitle(): String? = "Thông tin cá nhân"
    override fun useToolbar() = true
    override fun showBackButton() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val fullName = view.findViewById<EditText>(R.id.edtFullName)
        val email = view.findViewById<EditText>(R.id.edtEmail)
        val phone = view.findViewById<EditText>(R.id.edtPhone)
        val dob = view.findViewById<EditText>(R.id.edtDob)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)

        imgAvatar = view.findViewById(R.id.imgAvatar)

        val user = SharedPrefManager.getUserProfile(requireContext())
        user?.let {
            fullName.setText(it.optString("full_name", ""))
            email.setText(it.optString("mail", ""))
            phone.setText(it.optString("phoneNumber", ""))
            dob.setText(it.optString("dateOfBirth", ""))

            val avatarUrl = it.optString("avatar", "")
            if (avatarUrl.isNotEmpty()) {
                try {
                    imgAvatar.setImageURI(Uri.parse(avatarUrl))
                } catch (_: Exception) {}
            }
        }

        imgAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        btnSave.setOnClickListener {
            DialogUtils.showWarningDialog(
                context = requireContext(),
                message = "Bạn có chắc chắn muốn cập nhật thông tin cá nhân?",
                onConfirm = {
                    val updatedUser = mapOf(
                        "id" to SharedPrefManager.getUserId(requireContext()).toString(),
                        "full_name" to fullName.text.toString(),
                        "mail" to email.text.toString(),
                        "phoneNumber" to phone.text.toString(),
                        "dateOfBirth" to dob.text.toString(),
                        "avatar" to (avatarUri?.toString() ?: "")
                    )

                    lifecycleScope.launch {
                        try {
                            val response = RetrofitClient.apiService.updateProfile(updatedUser)
                            if (response.isSuccessful) {
                                SharedPrefManager.updateUserProfile(
                                    requireContext(),
                                    JSONObject(updatedUser)
                                )
                                DialogUtils.showSuccessDialog(
                                    context = requireContext(),
                                    message = "Cập nhật thông tin thành công!",
                                    showCreateNew = false
                                )
                            } else {
                                val errorBody = response.errorBody()?.string()
                                android.util.Log.e("PROFILE_UPDATE", "Lỗi cập nhật: $errorBody")
                                DialogUtils.showErrorDialog(requireContext(), "Cập nhật thất bại!")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("PROFILE_UPDATE", "Exception: ${e.message}", e)
                            DialogUtils.showErrorDialog(requireContext(), "Lỗi kết nối server")
                        }
                    }
                }
            )
        }

        btnChangePassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, ChangePasswordFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            avatarUri = data.data
            avatarUri?.let {
                val imageStream: InputStream? =
                    requireContext().contentResolver.openInputStream(it)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                imgAvatar.setImageBitmap(selectedImage)
            }
        }
    }
}