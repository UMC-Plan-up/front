package com.example.planup.signup.ui

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentLoginPasswordBinding
import com.example.planup.signup.SignupActivity

class LoginPasswordFragment : Fragment() {

    private var _binding: FragmentLoginPasswordBinding? = null
    private val binding get() = _binding!!

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disableNextButton()

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        /* 클릭 시 ic_eye_off ↔ ic_eye_on */
        binding.eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(binding.passwordEditText, binding.eyeIcon, isPasswordVisible)
        }

        binding.eyeIconConfirm.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(binding.confirmPasswordEditText, binding.eyeIconConfirm, isConfirmPasswordVisible)
        }

        // 비밀번호 입력 변화 감지
        binding.passwordEditText.addTextChangedListener {
            validateConditions()
        }

        // 비밀번호 확인 입력 변화 감지
        binding.confirmPasswordEditText.addTextChangedListener {
            validateConditions()
        }

        // 다음 버튼 클릭 → LoginSentEmailFragment로 이동
        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {  // 활성화된 경우만
                openNextStep()
            }
        }

        /* EditText 외 영역 터치 시 키보드 숨기기 */
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.passwordEditText.isFocused) {
                    binding.passwordEditText.clearFocus()
                    hideKeyboard()
                }
                if (binding.confirmPasswordEditText.isFocused) {
                    binding.confirmPasswordEditText.clearFocus()
                    hideKeyboard()
                }
                view.performClick()  // 접근성 이벤트
            }
            false
        }
    }

    /* LoginSentEmailFragment로 이동하는 메서드 */
    private fun openNextStep() {
        val password = binding.passwordEditText.text.toString()

        // (1) SignupActivity에 password 저장
        val activity = requireActivity() as SignupActivity
        activity.password = password

        // (2) LoginSentEmailFragment로 이동
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.signup_container, LoginSentEmailFragment())
            .addToBackStack(null)
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
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        // (1) 길이 조건 (8~20자)
        val isLengthValid = password.length in 8..20
        updateConditionUI(binding.checkLengthIcon, binding.lengthCondition, isLengthValid)

        // (2) 숫자 + 특수문자 포함 조건
        val hasNumber = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        val isComplexValid = hasNumber && hasSpecialChar
        updateConditionUI(binding.checkComplexIcon, binding.complexCondition, isComplexValid)

        // (3) 비밀번호 일치
        val isMatch = password.isNotEmpty() && password == confirmPassword
        updateConditionUI(binding.checkMatchIcon, binding.matchCondition, isMatch)

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
    private fun enableNextButton() {
        binding.nextButton.isEnabled = true
        binding.nextButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.blue_200)
    }

    private fun disableNextButton() {
        binding.nextButton.isEnabled = false
        binding.nextButton.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.black_200)
    }

    /* 키보드 숨기는 메서드 */
    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}