package com.example.btl_mad.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.btl_mad.R

object DialogUtils {

    fun showSuccessDialog(
        context: Context,
        message: String,
        onBack: (() -> Unit)? = null,
        showCreateNew: Boolean = true
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.success_dialog_transaction, null)
        val tvMessage = dialogView.findViewById<TextView>(R.id.textView)
        val btnBack = dialogView.findViewById<Button>(R.id.btnBack)
        val btnCreateNew = dialogView.findViewById<Button>(R.id.btnCreateNew)
        val imgIcon = dialogView.findViewById<ImageView>(R.id.iconImage)

        tvMessage.text = message
        imgIcon.setImageResource(R.drawable.success_icon)
        btnCreateNew.visibility = View.GONE
        btnBack.text = "Đóng"

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnBack.setOnClickListener {
            dialog.dismiss()
            onBack?.invoke()
        }

        dialog.show()
    }

    fun showWarningDialog(
        context: Context,
        message: String,
        confirmText: String = "Xác nhận",
        cancelText: String = "Huỷ",
        onConfirm: () -> Unit
    ) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.warning_dialog, null)
        val tvMessage = dialogView.findViewById<TextView>(R.id.textView)
        val btnBack = dialogView.findViewById<Button>(R.id.btnBack)
        val btnCreateNew = dialogView.findViewById<Button>(R.id.btnCreateNew)
        val imgIcon = dialogView.findViewById<ImageView>(R.id.iconImage)

        tvMessage.text = message
        imgIcon.setImageResource(R.drawable.warning_icon)

        btnBack.text = cancelText
        btnCreateNew.text = confirmText

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        btnBack.setOnClickListener { dialog.dismiss() }
        btnCreateNew.setOnClickListener {
            dialog.dismiss()
            onConfirm()
        }

        dialog.show()
    }

    fun showErrorDialog(context: Context, message: String) {
        AlertDialog.Builder(context)
            .setTitle("Lỗi")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
