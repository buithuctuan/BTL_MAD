package com.example.btl_mad.ui.transaction

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.FileProvider
import com.example.btl_mad.R
import com.example.btl_mad.data.TransactionType
import com.example.btl_mad.ui.fund.AddFundActivity
import com.example.btl_mad.ui.transaction.CategoryAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

object TransactionDialogs {

    fun showImagePickerDialog(
        activity: Activity,
        externalCacheDir: File?,
        packageName: String,
        onImageSelected: (Uri) -> Unit,
        launchCamera: (Intent) -> Unit,
        launchGallery: () -> Unit
    ) {
        val options = arrayOf("Chọn ảnh từ thư viện", "Chụp ảnh mới")
        MaterialAlertDialogBuilder(activity)
            .setTitle("Chọn ảnh")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> launchGallery()
                    1 -> {
                        val imageFile = File.createTempFile("camera_", ".jpg", externalCacheDir)
                        val cameraUri = FileProvider.getUriForFile(activity, "$packageName.fileprovider", imageFile)
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        onImageSelected(cameraUri)
                        launchCamera(takePictureIntent)
                    }
                }
            }
            .show()
    }

    fun showCategoryPickerDialog(
        activity: Activity,
        transactionTypes: List<TransactionType>,
        onCategorySelected: (TransactionType) -> Unit
    ) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.popup_category_picker, null)
        val dialog = MaterialAlertDialogBuilder(activity)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val gridView = dialogView.findViewById<GridView>(R.id.categoryGridView)
        val adapter = CategoryAdapter(activity, transactionTypes)
        gridView.adapter = adapter

        gridView.setOnItemClickListener { _, _, position, _ ->
            onCategorySelected(transactionTypes[position])
            dialog.dismiss()
        }

        val addCategoryLayout = dialogView.findViewById<LinearLayout>(R.id.addCategoryLayout)
        addCategoryLayout.setOnClickListener {
            dialog.dismiss()
            activity.startActivity(Intent(activity, AddFundActivity::class.java))
        }

        dialog.show()
        dialog.window?.let { window ->
            val params = window.attributes
            params.gravity = Gravity.BOTTOM
            params.width = android.view.WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = params
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }

    }
}
