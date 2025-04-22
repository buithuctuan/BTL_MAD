package com.example.btl_mad.ui.notification

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.data.Notification
import com.example.btl_mad.ui.notification.adapter.NotificationAdapter

class NotificationDialogFragment(private val notifications: List<Notification>) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_notification_popup)

        // Làm nền mờ
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.4f)

        // RecyclerView hiển thị thông báo
        val recyclerView = dialog.findViewById<RecyclerView>(R.id.rvNotificationList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = NotificationAdapter(notifications)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Hiển thị gần icon thông báo (nếu muốn tuỳ chỉnh vị trí)
        dialog?.window?.setGravity(Gravity.END or Gravity.TOP)

        // Tùy chỉnh vị trí chính xác nếu cần
        val params = dialog?.window?.attributes
        params?.y = 150 // dịch xuống từ top (px)
        params?.x = 32  // dịch từ phải sang trái (px)
        dialog?.window?.attributes = params
    }
}
