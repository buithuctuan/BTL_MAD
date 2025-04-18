package com.example.btl_mad.ui.signup

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.Question
import com.example.btl_mad.data.User
import com.example.btl_mad.ui.login.LoginActivity
import com.example.btl_mad.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etDob: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhoneNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var ivTogglePassword: ImageView
    private lateinit var rgGender: RadioGroup
    private lateinit var spinnerSecurityQuestion: Spinner
    private lateinit var etSecurityAnswer: EditText
    private lateinit var etAvatar: EditText
    private var isPasswordVisible = false
    private var questions: List<Question> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etFullName = findViewById(R.id.etFullName)
        etDob = findViewById(R.id.etDob)
        etEmail = findViewById(R.id.etEmail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etPassword = findViewById(R.id.etPassword)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)
        rgGender = findViewById(R.id.rgGender)
        spinnerSecurityQuestion = findViewById(R.id.spinnerSecurityQuestion)
        etSecurityAnswer = findViewById(R.id.etSecurityAnswer)
        etAvatar = findViewById(R.id.etAvatar)

        // Lấy danh sách câu hỏi từ API
        loadSecurityQuestions()

        // Xử lý hiện/ẩn mật khẩu
        ivTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivTogglePassword.setImageResource(android.R.drawable.ic_menu_view)
            }
            etPassword.setSelection(etPassword.text.length)
        }

        // Xử lý chọn ngày sinh
        etDob.setOnClickListener {
            showDatePicker()
        }

        // Xử lý nút Đăng ký
        findViewById<View>(R.id.btnSignUp).setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val dob = etDob.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val selectedGenderId = rgGender.checkedRadioButtonId
            val selectedQuestionPosition = spinnerSecurityQuestion.selectedItemPosition
            val securityAnswer = etSecurityAnswer.text.toString().trim()
            val avatar = etAvatar.text.toString().trim()

            // Validate đầu vào
            if (fullName.isEmpty() || dob.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || selectedGenderId == -1 || selectedQuestionPosition == 0 || securityAnswer.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate họ tên
            if (fullName.length < 3) {
                Toast.makeText(this, "Họ tên phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate email
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate số điện thoại
            if (!isValidVietnamPhoneNumber(phoneNumber)) {
                Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate mật khẩu
            if (password.length < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate câu trả lời bảo mật
            if (securityAnswer.length < 3) {
                Toast.makeText(this, "Câu trả lời bảo mật phải có ít nhất 3 ký tự", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lấy giới tính
            val gender = if (selectedGenderId == R.id.rbMale) "Nam" else "Nữ"

            // Chuyển định dạng ngày sinh sang "yyyy-MM-dd"
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dobFormatted = outputFormat.format(inputFormat.parse(dob)!!)

            // Lấy question_id từ vị trí được chọn trong Spinner
            val selectedQuestionId = if (selectedQuestionPosition > 0) {
                questions[selectedQuestionPosition - 1].id
            } else {
                0
            }

            // Tạo đối tượng User
            val user = User(
                username = email.split("@")[0],
                pass_ = password,
                full_name = fullName,
                dateOfBirth = dobFormatted,
                mail = email,
                phone = phoneNumber,
                sex = gender,
                question_id = selectedQuestionId,
                answer_for_forgot_password = securityAnswer,
                avatar = if (avatar.isEmpty()) null else avatar
            )

            // Gọi API đăng ký
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = RetrofitClient.apiService.registerUser(user)
                    if (response.isSuccessful) {
                        Toast.makeText(this@SignUpActivity, response.body()?.get("message"), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignUpActivity, response.errorBody()?.string(), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@SignUpActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Xử lý "Đã có tài khoản? Đăng nhập"
        findViewById<View>(R.id.tvLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun loadSecurityQuestions() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = RetrofitClient.apiService.getQuestions()
                if (response.isSuccessful) {
                    questions = response.body() ?: emptyList()
                    val questionDetails = mutableListOf("Chọn câu hỏi bảo mật")
                    questionDetails.addAll(questions.map { it.question_detail })
                    val adapter = ArrayAdapter(this@SignUpActivity, android.R.layout.simple_spinner_item, questionDetails)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerSecurityQuestion.adapter = adapter
                } else {
                    Toast.makeText(this@SignUpActivity, "Không thể tải câu hỏi bảo mật", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignUpActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                etDob.setText(dateFormat.format(selectedDate.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        return email.matches(emailPattern.toRegex())
    }

    private fun isValidVietnamPhoneNumber(phoneNumber: String): Boolean {
        val vietnamPhonePattern = "^(03|05|07|08|09)[0-9]{8}$"
        return phoneNumber.matches(vietnamPhonePattern.toRegex())
    }
}