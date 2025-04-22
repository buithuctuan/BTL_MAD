package com.example.btl_mad.ui.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.data.Notification

class NotificationAdapter(private var list: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotiViewHolder>() {

    inner class NotiViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvContent: TextView = view.findViewById(R.id.tvContent)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotiViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotiViewHolder, position: Int) {
        val item = list[position]
        holder.tvTitle.text = item.title
        holder.tvContent.text = item.content
        holder.tvDate.text = item.date
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Notification>) {
        list = newList
        notifyDataSetChanged()
    }
}
