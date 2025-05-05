package com.example.btl_mad.ui.signup

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.btl_mad.R
import com.example.btl_mad.api.RetrofitClient
import com.example.btl_mad.data.Question

import com.example.btl_mad.data.User
import com.example.btl_mad.data.UserRegisterRequest
import com.example.btl_mad.ui.login.LoginActivity
import com.example.btl_mad.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
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
    private lateinit var sharedPreferences: SharedPreferences
    private var isPasswordVisible = false
    private var questions: List<Question> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etUsername = findViewById(R.id.etUsername)
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
        sharedPreferences = getSharedPreferences("SecurityQuestions", MODE_PRIVATE)

        // Lấy danh sách câu hỏi từ API hoặc cache
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
            val username = etUsername.text.toString().trim()
            val fullName = etFullName.text.toString().trim()
            val dob = etDob.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phoneNumber = etPhoneNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val selectedGenderId = rgGender.checkedRadioButtonId
            val selectedQuestionPosition = spinnerSecurityQuestion.selectedItemPosition
            val securityAnswer = etSecurityAnswer.text.toString().trim()
            val avatar = etAvatar.text.toString().trim()

            // Validate đầu vào cơ bản
            if (username.isEmpty() || fullName.isEmpty() || dob.isEmpty() || email.isEmpty() ||
                phoneNumber.isEmpty() || password.isEmpty() || selectedGenderId == -1 ||
                selectedQuestionPosition == 0 || securityAnswer.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lấy giới tính
            val gender = when (selectedGenderId) {
                R.id.rbMale -> "male"
                R.id.rbFemale -> "female"
                else -> {
                    Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Chuyển định dạng ngày sinh sang "yyyy-MM-dd"
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dobFormatted = try {
                outputFormat.format(inputFormat.parse(dob)!!)
            } catch (e: Exception) {
                Toast.makeText(this, "Định dạng ngày sinh không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lấy question_id từ vị trí được chọn trong Spinner
            val selectedQuestionId = if (selectedQuestionPosition > 0) {
                questions[selectedQuestionPosition - 1].id
            } else {
                Toast.makeText(this, "Vui lòng chọn câu hỏi bảo mật", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo đối tượng User
            val user = UserRegisterRequest(
                username = username,
                pass_ = password,
                full_name = fullName,
                dateOfBirth = dobFormatted,
                mail = email,
                phoneNumber = phoneNumber,
                sex = gender,
                question_id = selectedQuestionId,
                answer_for_forgot_password = securityAnswer,
                avatar = if (avatar.isEmpty()) null else avatar
            )

            // Gọi API đăng ký
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitClient.apiService.registerUser(user)
                    }
                    if (response.isSuccessful) {
                        val message = response.body()?.get("message") as? String ?: "Đăng ký thành công"
                        Toast.makeText(this@SignUpActivity, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "Đăng ký thất bại"
                        Toast.makeText(this@SignUpActivity, errorBody, Toast.LENGTH_LONG).show()
                    }
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string() ?: "Lỗi server"
                    Toast.makeText(this@SignUpActivity, errorBody, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@SignUpActivity, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
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
        // Kiểm tra cache
        val cachedQuestions = sharedPreferences.getString("questions", null)
        if (cachedQuestions != null) {
            val type = object : TypeToken<List<Question>>() {}.type
            questions = Gson().fromJson(cachedQuestions, type)
            setupSpinner()
            return
        }

        // Tải từ API
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getQuestions()
                }
                if (response.isSuccessful) {
                    questions = response.body() ?: emptyList()
                    // Lưu vào cache
                    sharedPreferences.edit().putString("questions", Gson().toJson(questions)).apply()
                    setupSpinner()
                } else {
                    Toast.makeText(this@SignUpActivity, "Không thể tải câu hỏi: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignUpActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinner() {
        val questionDetails = mutableListOf("Chọn câu hỏi bảo mật")
        questionDetails.addAll(questions.map { it.question_detail })
        val adapter = ArrayAdapter(this@SignUpActivity, android.R.layout.simple_spinner_item, questionDetails)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSecurityQuestion.adapter = adapter
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