package com.example.btl_mad.ui.fund

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_mad.R
import com.example.btl_mad.data.Fund
import com.example.btl_mad.databinding.ItemFundBinding
import java.text.DecimalFormat


class FundAdapter(private val fundList: List<Fund>) :
    RecyclerView.Adapter<FundAdapter.FundViewHolder>() {

    // Tạo ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundViewHolder {
        val binding = ItemFundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FundViewHolder(binding)
    }

    // Liên kết dữ liệu với ViewHolder
    override fun onBindViewHolder(holder: FundViewHolder, position: Int) {
        val fund = fundList[position]
        holder.bind(fund)
    }

    override fun getItemCount(): Int = fundList.size

    // ViewHolder để hiển thị thông tin Quỹ
    class FundViewHolder(private val binding: ItemFundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(fund: Fund) {
            val formatter = DecimalFormat("#,###")
            binding.root.tag = fund.id
            // Hiển thị tên quỹ, số tiền, và các thông tin khác
            binding.categoryTextView.text = fund.name
            binding.amountTextView.text = formatter.format(fund.budget)
            binding.amountTextView1.text = formatter.format(fund.budget)
            binding.out.text = formatter.format(fund.total_expenses)
            binding.conlai.text = formatter.format(fund.days_left)
            binding.hanmucngay.text = formatter.format(fund.spending_limit) + "VNĐ/ngày"

            val iconFileName = fund.icon  // Đây là tên file không có đuôi (ví dụ: "fund_icon_1")
            val resourceId = binding.imageView.context.resources.getIdentifier(
                iconFileName,  // Không có đuôi ".png"
                "drawable",  // Thư mục tài nguyên drawable
                binding.imageView.context.packageName
            )

            if (resourceId != 0) {
                binding.imageView.setImageResource(resourceId)
            } else {
                binding.imageView.setImageResource(R.drawable.fund_icon_4)
            }
        }
    }
}
