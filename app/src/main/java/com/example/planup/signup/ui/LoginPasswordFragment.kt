package com.example.planup.signup.ui

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R

class LoginPasswordFragment : Fragment(R.layout.fragment_login_password) {

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

    private lateinit var nextButton: AppCompatButton

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        val backIcon = view.findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

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

        nextButton = view.findViewById(R.id.nextButton)
        disableNextButton()


        /* 클릭 시 ic_eye_off ↔ ic_eye_on */
        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(passwordEditText, eyeIcon, isPasswordVisible)
        }

        eyeIconConfirm.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(confirmPasswordEditText, eyeIconConfirm, isConfirmPasswordVisible)
        }

        // 비밀번호 입력 변화 감지
        passwordEditText.addTextChangedListener {
            validateConditions()
        }

        // 비밀번호 확인 입력 변화 감지
        confirmPasswordEditText.addTextChangedListener {
            validateConditions()
        }

        // 다음 버튼 클릭 → LoginSentEmailFragment로 이동
        nextButton.setOnClickListener {
            if (nextButton.isEnabled) {  // 활성화된 경우만 이동 가능
                openNextStep()
            }
        }
    }


    /* LoginSentEmailFragment로 이동하는 메서드 */
    private fun openNextStep() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, LoginSentEmailFragment()) // 다음 단계로 이동
            .addToBackStack(null) // 뒤로가기 가능
            .commit()
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

        // (1) 길이 조건 (8~20자)
        val isLengthValid = password.length in 8..20
        updateConditionUI(checkLengthIcon, lengthCondition, isLengthValid)

        // (2) 숫자 + 특수문자 포함 조건
        val hasNumber = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val isComplexValid = hasNumber && hasSpecialChar
        updateConditionUI(checkComplexIcon, complexCondition, isComplexValid)

        // (3) 비밀번호 일치
        val isMatch = password.isNotEmpty() && password == confirmPassword
        updateConditionUI(checkMatchIcon, matchCondition, isMatch)

        // 세 조건 모두 충족하면 버튼 활성화
        if (isLengthValid && isComplexValid && isMatch) {
            enableNextButton()
        } else {
            disableNextButton()
        }
    }


    /* 조건에 따라 색상 변경 */
    private fun updateConditionUI(icon: ImageView, textView: TextView, isValid: Boolean) {
        if (isValid) {
            // 조건 만족
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green_200))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_200))
        } else {
            // 조건 불만족
            val grayColor = Color.parseColor("#ADADAD")
            icon.setColorFilter(grayColor)
            textView.setTextColor(grayColor)
        }
    }


    /* 다음 버튼 활성 ↔ 비활성 */
    private fun enableNextButton() {  // 다음 버튼 활성화
        nextButton.isEnabled = true
        nextButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(),
            R.color.blue_200
        )
    }

    private fun disableNextButton() {  // 다음 버튼 비활성화
        nextButton.isEnabled = false
        nextButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(),
            R.color.black_200
        )
    }
}
