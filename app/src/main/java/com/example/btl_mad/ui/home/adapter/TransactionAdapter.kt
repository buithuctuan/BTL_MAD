//package com.example.btl_mad.ui.home.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.btl_mad.R
//import com.example.btl_mad.data.Transaction
//
//class TransactionAdapter(private val list: List<Transaction>) :
//    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//
//    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tvTitle: TextView = itemView.findViewById(R.id.tvTransactionTitle)
//        val tvAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)
//        val tvDate: TextView = itemView.findViewById(R.id.tvTransactionDate)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_transaction_home, parent, false)
//        return TransactionViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        val item = list[position]
//        holder.tvTitle.text = item.title
//        holder.tvAmount.text = "${item.amount} VND"
//        holder.tvDate.text = item.date
//    }
//
//    override fun getItemCount(): Int = list.size
//}
