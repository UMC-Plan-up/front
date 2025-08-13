package com.example.planup.password.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentFindPasswordBinding
import com.example.planup.databinding.PopupEmailBinding
import com.example.planup.login.ui.LoginActivity
import com.example.planup.password.ResetPasswordActivity

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
                // 형식이 틀리면 이메일 형식 에러 표시
                binding.emailFormatErrorText.visibility = View.VISIBLE
                binding.emailNotFoundErrorText.visibility = View.GONE
                disableNextButton()
            }
        }

        // 다음 버튼 클릭 → 이메일 형식만 확인하고 "바로" 다음 화면으로 이동 (자동 발송은 다음 화면에서 처리)
        binding.nextButton.setOnClickListener {
            if (!binding.nextButton.isEnabled) return@setOnClickListener
            val email = binding.emailEditText.text.toString().trim()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showEmailFormatError()
                disableNextButton()
                return@setOnClickListener
            }

            // 다음 화면으로 이동
            (requireActivity() as ResetPasswordActivity)
                .navigateToFragment(FindPasswordEmailSentFragment.newInstance(email))
        }

        // 뒤로가기 아이콘 클릭 → 로그인 화면 이동
        binding.backIcon.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
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

    // 이메일 형식 에러 표시
    private fun showEmailFormatError() {
        binding.emailFormatErrorText.visibility = View.VISIBLE
        binding.emailNotFoundErrorText.visibility = View.GONE
    }

    // 가입되지 않은 이메일 에러 표시
    private fun showEmailNotFoundError() {
        binding.emailFormatErrorText.visibility = View.GONE
        binding.emailNotFoundErrorText.visibility = View.VISIBLE
    }

    // 모든 에러 문구 숨김
    private fun hideAllErrors() {
        binding.emailFormatErrorText.visibility = View.GONE
        binding.emailNotFoundErrorText.visibility = View.GONE
    }

    // 다음 버튼 활성화
    private fun enableNextButton() {
        binding.nextButton.isEnabled = true
        binding.nextButton.backgroundTintList =
            requireContext().getColorStateList(R.color.blue_200)
    }

    // 다음 버튼 비활성화
    private fun disableNextButton() {
        binding.nextButton.isEnabled = false
        binding.nextButton.backgroundTintList =
            requireContext().getColorStateList(R.color.black_200)
    }

    // 이메일 도메인 선택 팝업
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

        // 도메인 클릭 시 이메일 입력창에 추가
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

    // 키보드 숨김
    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
