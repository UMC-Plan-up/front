package com.example.planup

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment

class ResetPasswordFragment : Fragment(R.layout.fragment_reset_password) {

    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText

    private lateinit var eyeIcon: ImageView
    private lateinit var eyeIconConfirm: ImageView

    private lateinit var checkLengthIcon: ImageView
    private lateinit var lengthCondition: TextView
    private lateinit var checkComplexIcon: ImageView
    private lateinit var complexCondition: TextView
    private lateinit var checkMatchIcon: ImageView
    private lateinit var matchCondition: TextView

    private lateinit var passwordMatchHint: TextView
    private lateinit var nextButton: AppCompatButton

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userEmail = arguments?.getString("email")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 초기화
        passwordEditText = view.findViewById(R.id.passwordEditText)
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText)

        eyeIcon = view.findViewById(R.id.eyeIcon)
        eyeIconConfirm = view.findViewById(R.id.eyeIconConfirm)

        checkLengthIcon = view.findViewById(R.id.checkLengthIcon)
        lengthCondition = view.findViewById(R.id.lengthCondition)
        checkComplexIcon = view.findViewById(R.id.checkComplexIcon)
        complexCondition = view.findViewById(R.id.complexCondition)
        checkMatchIcon = view.findViewById(R.id.checkMatchIcon)
        matchCondition = view.findViewById(R.id.matchCondition)

        passwordMatchHint = view.findViewById(R.id.passwordMatchHint)
        nextButton = view.findViewById(R.id.nextButton)


        disableNextButton()
        hideAllConditions()

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        val backIcon = view.findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            val prevFragment = FindPasswordEmailSentFragment().apply {
                arguments = Bundle().apply {
                    putString("email", userEmail)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.resetPasswordContainer, prevFragment)
                .addToBackStack(null)
                .commit()
        }

        /* 클릭 시 비밀번호 보이기/숨기기 (ic_eye_off ↔ ic_eye_on) */
        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(passwordEditText, eyeIcon, isPasswordVisible)
        }

        eyeIconConfirm.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(confirmPasswordEditText, eyeIconConfirm, isConfirmPasswordVisible)
        }

        /* 비밀번호 입력창 클릭 → 길이/복잡도 조건 표시 */
        passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showLengthAndComplexAsGray()
            }
        }

        /* 비밀번호 확인 입력창 클릭 → 일치 조건 표시 */
        confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                passwordMatchHint.visibility = View.GONE
                showMatchConditionAsGray()
            }
        }

        // 입력 변화 감지
        passwordEditText.addTextChangedListener {
            validateConditions()
        }
        confirmPasswordEditText.addTextChangedListener {
            validateConditions()
        }

        /* 완료 버튼 클릭 → popup_reset 띄우기 */
        nextButton.setOnClickListener {
            showResetCompleteDialog()
        }
    }

    /* 비밀번호 보이기 ↔ 숨기기 */
    private fun togglePasswordVisibility(editText: EditText, icon: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            icon.setImageResource(R.drawable.ic_eye_on)
        } else {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            icon.setImageResource(R.drawable.ic_eye_off)
        }
        editText.setSelection(editText.text?.length ?: 0)
    }

    /* 비밀번호 조건 검사 */
    private fun validateConditions() {
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        // (1) 길이 조건: 8~20자
        val isLengthValid = password.length in 8..20
        updateConditionColor(checkLengthIcon, lengthCondition, isLengthValid)

        // (2) 숫자 & 특수문자 포함 조건
        val hasNumber = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        val isComplexValid = hasNumber && hasSpecial
        updateConditionColor(checkComplexIcon, complexCondition, isComplexValid)

        // (3) 비밀번호 일치 조건
        val isMatch = password.isNotEmpty() && password == confirmPassword
        updateConditionColor(checkMatchIcon, matchCondition, isMatch)

        // 세 조건 모두 만족하면 버튼 활성화
        if (isLengthValid && isComplexValid && isMatch) {
            enableNextButton()
        } else {
            disableNextButton()
        }
    }

    /* 조건 만족 여부에 따라 색상 변경 */
    private fun updateConditionColor(icon: ImageView, textView: TextView, isValid: Boolean) {
        val context = requireContext()
        if (isValid) {
            icon.setColorFilter(ContextCompat.getColor(context, R.color.green_200))
            textView.setTextColor(ContextCompat.getColor(context, R.color.green_200))
        } else {
            icon.setColorFilter(ContextCompat.getColor(context, R.color.black_300))
            textView.setTextColor(ContextCompat.getColor(context, R.color.black_300))
        }
    }

    /* 처음엔 조건 숨기기 */
    private fun hideAllConditions() {
        checkLengthIcon.visibility = View.GONE
        lengthCondition.visibility = View.GONE
        checkComplexIcon.visibility = View.GONE
        complexCondition.visibility = View.GONE
        checkMatchIcon.visibility = View.GONE
        matchCondition.visibility = View.GONE
    }

    /* 비밀번호 입력창 클릭 시 → 길이/복잡도 조건 표시 */
    private fun showLengthAndComplexAsGray() {
        val gray = ContextCompat.getColor(requireContext(), R.color.black_300)
        checkLengthIcon.setColorFilter(gray)
        lengthCondition.setTextColor(gray)
        checkComplexIcon.setColorFilter(gray)
        complexCondition.setTextColor(gray)

        checkLengthIcon.visibility = View.VISIBLE
        lengthCondition.visibility = View.VISIBLE
        checkComplexIcon.visibility = View.VISIBLE
        complexCondition.visibility = View.VISIBLE
    }

    /* 비밀번호 확인 입력창 클릭 시 → 일치 조건 표시 */
    private fun showMatchConditionAsGray() {
        val gray = ContextCompat.getColor(requireContext(), R.color.black_300)
        checkMatchIcon.setColorFilter(gray)
        matchCondition.setTextColor(gray)

        checkMatchIcon.visibility = View.VISIBLE
        matchCondition.visibility = View.VISIBLE
    }

    /* 완료 버튼 활성 ↔ 비활성 */
    private fun enableNextButton() {
        nextButton.isEnabled = true
        nextButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.blue_200)
    }

    private fun disableNextButton() {
        nextButton.isEnabled = false
        nextButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.black_200)
    }

    /* 팝업 띄우기 → 확인 버튼 누르면 LoginActivity로 이동 */
    private fun showResetCompleteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.popup_reset, null)

        val safeEmail = userEmail ?: "이메일"
        val popupMessage = dialogView.findViewById<TextView>(R.id.resetCompleteDesc)
        popupMessage.text = "입력하신 $safeEmail 계정의\n비밀번호가 변경되었어요."

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val popupConfirmButton = dialogView.findViewById<AppCompatButton>(R.id.confirmButton)
        popupConfirmButton.setOnClickListener {
            dialog.dismiss()
            goToLoginActivity()
        }

        dialog.show()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 화면 너비의 80% 비율로 가로 폭 설정
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val dialogWidth = (screenWidth * 0.8).toInt()

        dialog.window?.setLayout(
            dialogWidth, // 가로 폭: 화면의 80%
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }


    /* LoginActivity로 이동 */
    private fun goToLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
