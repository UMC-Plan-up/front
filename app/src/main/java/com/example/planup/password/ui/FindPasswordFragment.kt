package com.example.planup.password.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.FragmentFindPasswordBinding
import com.example.planup.databinding.PopupEmailBinding
import com.example.planup.login.LoginActivityNew
import com.example.planup.network.RetrofitInstance
import com.example.planup.password.ResetPasswordActivity
import com.example.planup.password.data.PasswordChangeEmailRequestDto
import kotlinx.coroutines.launch

class FindPasswordFragment : Fragment() {

    private var _binding: FragmentFindPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        disableNextButton()
        hideAllErrors()

        // 이메일 입력 시 형식 체크
        binding.emailEditText.addTextChangedListener {
            val email = it.toString().trim()
            val isFormatValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (isFormatValid) {
                hideAllErrors()
                enableNextButton()
            } else {
                showEmailFormatError()
                disableNextButton()
            }
        }

        binding.nextButton.setOnClickListener {
            if (!binding.nextButton.isEnabled) return@setOnClickListener

            val email = binding.emailEditText.text.toString().trim()
            val isFormatValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (!isFormatValid) {
                showEmailFormatError()
                disableNextButton()
                return@setOnClickListener
            }

            // 호출 동안 버튼 잠금 & 에러 숨김
            binding.nextButton.isEnabled = false
            hideAllErrors()

            viewLifecycleOwner.lifecycleScope.launch {
                Log.d(TAG, "API 호출 시작: $email")
                runCatching {
                    RetrofitInstance.userApi
                        .sendPasswordChangeEmail(
                            PasswordChangeEmailRequestDto(
                                email = email,
                                isLoggedIn = false
                            )
                        )
                }.onSuccess { res ->
                    val body = res.body()
                    if (res.isSuccessful && body?.isSuccess == true) {
                        (requireActivity() as ResetPasswordActivity)
                            .navigateToFragment(
                                FindPasswordEmailSentFragment.newInstance(email).apply {
                                    arguments?.putBoolean("shouldSend", false)
                                }
                            )
                    } else {
                        // 가입되지 않은 이메일 또는 기타 실패
                        val msg = body?.message.orEmpty()
                        Log.d(TAG, "실패: 메시지=$msg")
                        if (msg.contains("존재하지") || msg.contains("없")) {
                            showEmailNotFoundError()
                        } else {
                            showEmailNotFoundError()
                        }
                        binding.nextButton.isEnabled = true
                    }
                }.onFailure { e ->
                    // 네트워크/예외
                    Log.e(TAG, "API 호출 예외 발생", e)
                    showEmailNotFoundError()
                    binding.nextButton.isEnabled = true
                }
            }
        }

        // 뒤로가기 아이콘 → 로그인 화면
        binding.backIcon.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivityNew::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.emailDropdownIcon.setOnClickListener { showEmailDomainPopup() }

        // EditText 외부 터치 시 키보드 숨김
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && binding.emailEditText.isFocused) {
                binding.emailEditText.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }
    }

    // 에러/버튼 상태
    private fun showEmailFormatError() {
        binding.emailFormatErrorText.visibility = View.VISIBLE
        binding.emailNotFoundErrorText.visibility = View.GONE
    }

    private fun showEmailNotFoundError() {
        binding.emailFormatErrorText.visibility = View.GONE
        binding.emailNotFoundErrorText.visibility = View.VISIBLE
    }

    private fun hideAllErrors() {
        binding.emailFormatErrorText.visibility = View.GONE
        binding.emailNotFoundErrorText.visibility = View.GONE
    }

    private fun enableNextButton() {
        binding.nextButton.isEnabled = true
        binding.nextButton.backgroundTintList =
            requireContext().getColorStateList(R.color.blue_200)
    }

    private fun disableNextButton() {
        binding.nextButton.isEnabled = false
        binding.nextButton.backgroundTintList =
            requireContext().getColorStateList(R.color.black_200)
    }

    // 이메일 도메인 팝업
    private fun showEmailDomainPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupBinding = PopupEmailBinding.inflate(inflater)

        popupBinding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = popupBinding.root.measuredWidth

        val popupWindow = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val addDomain: (String) -> Unit = { domain ->
            val currentText = binding.emailEditText.text.toString()
            val updatedText = if (currentText.contains("@")) {
                currentText.substringBefore("@") + "@$domain"
            } else {
                "$currentText@$domain"
            }
            binding.emailEditText.setText(updatedText)
            binding.emailEditText.setSelection(updatedText.length)
            popupWindow.dismiss()
        }

        popupBinding.domainGmail.setOnClickListener { addDomain("gmail.com") }
        popupBinding.domainNaver.setOnClickListener { addDomain("naver.com") }
        popupBinding.domainKakao.setOnClickListener { addDomain("kakao.com") }

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 8f

        val offsetX = binding.emailEditText.width - popupWidth
        popupWindow.showAsDropDown(binding.emailEditText, offsetX, 0)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
