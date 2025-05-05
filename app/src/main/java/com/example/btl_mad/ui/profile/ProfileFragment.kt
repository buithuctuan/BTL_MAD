package com.example.btl_mad.ui.profile
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.example.btl_mad.R
import com.example.btl_mad.ui.BaseFragment
import com.example.btl_mad.ui.login.LoginActivity
import com.example.btl_mad.utils.SharedPrefManager


class ProfileFragment : BaseFragment() {

    override fun getLayoutId() = R.layout.fragment_profile
    override fun getToolbarTitle(): String? = "Tài khoản"
    override fun useToolbar() = true
    override fun showBackButton() = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvUserName).text =
            SharedPrefManager.getUserName(requireContext()) // nếu có lưu

        view.findViewById<TextView>(R.id.tvProfile).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, ProfileInfoFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<TextView>(R.id.tvSettings).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, SettingsFragment())
                .addToBackStack(null)
                .commit()

        }

        view.findViewById<TextView>(R.id.tvHelp).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame, HelpFragment())  // Bạn đã tạo HelpFragment rồi
                .addToBackStack(null)
                .commit()
        }


        view.findViewById<TextView>(R.id.tvLogout).setOnClickListener {
            SharedPrefManager.clearAll(requireContext())
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            Toast.makeText(context, "Cảm ơn bạn đã đánh giá $rating sao!", Toast.LENGTH_SHORT).show()

            // TODO: có thể gửi lên server, hoặc mở Google Play để đánh giá thực sự
        }
    }
}
