package com.planup.planup.password.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.planup.planup.R
import com.planup.planup.databinding.FragmentResetPasswordBinding
import com.planup.planup.databinding.PopupResetBinding
import com.planup.planup.login.ui.LoginActivityNew
import com.planup.planup.network.RetrofitInstance
import com.planup.planup.password.data.PasswordChangeRequest
import kotlinx.coroutines.launch

class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var userEmail: String? = null
    private var verificationToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userEmail = arguments?.getString("email")
        verificationToken = arguments?.getString("token")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disableNextButton()
        hideAllConditions()

        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN &&
                (binding.passwordEditText.isFocused || binding.confirmPasswordEditText.isFocused)
            ) {
                binding.passwordEditText.clearFocus()
                binding.confirmPasswordEditText.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }

        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /* 클릭 시 비밀번호 보이기/숨기기 */
        binding.eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(binding.passwordEditText, binding.eyeIcon, isPasswordVisible)
        }
        binding.eyeIconConfirm.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(
                binding.confirmPasswordEditText,
                binding.eyeIconConfirm,
                isConfirmPasswordVisible
            )
        }

        /* 비밀번호 입력창 클릭 → 길이/복잡도 조건 표시 */
        binding.passwordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showLengthAndComplexAsGray()
        }

        /* 비밀번호 확인 입력창 클릭 → 일치 조건 표시 */
        binding.confirmPasswordEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.passwordMatchHint.visibility = View.GONE
                showMatchConditionAsGray()
            }
        }

        // 입력 변화 감지
        binding.passwordEditText.addTextChangedListener { validateConditions() }
        binding.confirmPasswordEditText.addTextChangedListener { validateConditions() }

        /* 완료 버튼 클릭 */
        binding.nextButton.setOnClickListener {
            val pw = binding.passwordEditText.text.toString()
            val cpw = binding.confirmPasswordEditText.text.toString()

            val lenOk = pw.length in 8..20
            val complexOk = pw.any { it.isDigit() } && pw.any { !it.isLetterOrDigit() }
            val matchOk = pw.isNotEmpty() && pw == cpw

            if (!lenOk || !complexOk || !matchOk) {
                disableNextButton()
                return@setOnClickListener
            }

            updatePassword(pw)
        }
    }

    /* 비밀번호 보이기 ↔ 숨기기 */
    private fun togglePasswordVisibility(editText: EditText, icon: ImageView, isVisible: Boolean) {
        if (isVisible) {
            editText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            icon.setImageResource(R.drawable.ic_eye_on)
        } else {
            editText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            icon.setImageResource(R.drawable.ic_eye_off)
        }
        editText.setSelection(editText.text?.length ?: 0)
    }

    /* 비밀번호 조건 검사 */
    private fun validateConditions() {
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()

        // (1) 길이 조건: 8~20자
        val isLengthValid = password.length in 8..20
        updateConditionColor(binding.checkLengthIcon, binding.lengthCondition, isLengthValid)

        // (2) 숫자 & 특수문자 포함 조건
        val hasNumber = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        val isComplexValid = hasNumber && hasSpecial
        updateConditionColor(binding.checkComplexIcon, binding.complexCondition, isComplexValid)

        // (3) 비밀번호 일치 조건
        val isMatch = password.isNotEmpty() && password == confirmPassword
        updateConditionColor(binding.checkMatchIcon, binding.matchCondition, isMatch)

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
            icon.setColorFilter(ContextCompat.getColor(context, R.color.medium_sea_green))
            textView.setTextColor(ContextCompat.getColor(context, R.color.medium_sea_green))
        } else {
            icon.setColorFilter(ContextCompat.getColor(context, R.color.black_300))
            textView.setTextColor(ContextCompat.getColor(context, R.color.black_300))
        }
    }

    /* 비밀번호 변경 API 호출 */
    private fun updatePassword(newPassword: String) {
        binding.nextButton.isEnabled = false

        val token = verificationToken?.trim()
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "유효하지 않은 링크입니다.", Toast.LENGTH_SHORT).show()
            binding.nextButton.isEnabled = true
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val req = PasswordChangeRequest(newPassword = newPassword)
                val res = RetrofitInstance.userApi.changePassword(req)
                val body = res.body()

                if (res.isSuccessful && body?.isSuccess == true && body.result) {
                    showResetCompleteDialog()
                } else {
                    val msg = body?.message ?: res.errorBody()?.string() ?: "비밀번호 변경 실패"
                    Log.e("API_ERROR", "Password update failed: $msg")
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    binding.nextButton.isEnabled = true
                }
            } catch (e: Exception) {
                Log.e("NETWORK_ERROR", "updatePassword()", e)
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                binding.nextButton.isEnabled = true
            }
        }
    }


    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /* 처음엔 조건 숨기기 */
    private fun hideAllConditions() {
        binding.checkLengthIcon.visibility = View.GONE
        binding.lengthCondition.visibility = View.GONE
        binding.checkComplexIcon.visibility = View.GONE
        binding.complexCondition.visibility = View.GONE
        binding.checkMatchIcon.visibility = View.GONE
        binding.matchCondition.visibility = View.GONE
    }

    /* 비밀번호 입력창 클릭 시 → 길이/복잡도 조건 표시 */
    private fun showLengthAndComplexAsGray() {
        val gray = ContextCompat.getColor(requireContext(), R.color.black_300)
        binding.checkLengthIcon.setColorFilter(gray)
        binding.lengthCondition.setTextColor(gray)
        binding.checkComplexIcon.setColorFilter(gray)
        binding.complexCondition.setTextColor(gray)

        binding.checkLengthIcon.visibility = View.VISIBLE
        binding.lengthCondition.visibility = View.VISIBLE
        binding.checkComplexIcon.visibility = View.VISIBLE
        binding.complexCondition.visibility = View.VISIBLE
    }

    /* 비밀번호 확인 입력창 클릭 시 → 일치 조건 표시 */
    private fun showMatchConditionAsGray() {
        val gray = ContextCompat.getColor(requireContext(), R.color.black_300)
        binding.checkMatchIcon.setColorFilter(gray)
        binding.matchCondition.setTextColor(gray)

        binding.checkMatchIcon.visibility = View.VISIBLE
        binding.matchCondition.visibility = View.VISIBLE
    }

    /* 완료 버튼 활성 ↔ 비활성 */
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

    /* 팝업 띄우기 → 확인 버튼 누르면 LoginActivityNew로 이동 */
    private fun showResetCompleteDialog() {
        val popupBinding = PopupResetBinding.inflate(layoutInflater)

        val safeEmail = userEmail ?: "이메일"
        popupBinding.resetCompleteDesc.text = "입력하신 $safeEmail 계정의\n비밀번호가 변경되었어요."

        val dialog = AlertDialog.Builder(requireContext())
            .setView(popupBinding.root)
            .setCancelable(false)
            .create()

        popupBinding.confirmButton.setOnClickListener {
            dialog.dismiss()
            goToLoginActivity()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val dialogWidth = (screenWidth * 0.8).toInt()

        dialog.window?.setLayout(dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    /* LoginActivityNew로 이동 */
    private fun goToLoginActivity() {
        val intent = Intent(requireContext(), LoginActivityNew::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
