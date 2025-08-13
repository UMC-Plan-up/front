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
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.login.ui.LoginActivity
import com.example.planup.password.ResetPasswordActivity
import com.example.planup.databinding.FragmentFindPasswordBinding
import com.example.planup.databinding.PopupEmailBinding

class FindPasswordFragment : Fragment() {

    private var _binding: FragmentFindPasswordBinding? = null
    private val binding get() = _binding!!

    private val registeredEmails = listOf("user@gmail.com")

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

        binding.emailEditText.addTextChangedListener {
            val email = it.toString().trim()
            validateEmail(email)
        }

        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {
                (requireActivity() as ResetPasswordActivity)
                    .navigateToFragment(FindPasswordEmailSentFragment())
            }
        }

        binding.backIcon.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.emailDropdownIcon.setOnClickListener {
            showEmailDomainPopup()
        }

        // EditText 외부 터치 시 키보드 숨기기
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && binding.emailEditText.isFocused) {
                binding.emailEditText.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }
    }

    private fun validateEmail(email: String) {
        val isFormatValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (!isFormatValid) {
            showEmailFormatError()
            disableNextButton()
            return
        }

        if (!registeredEmails.contains(email)) {
            showEmailNotFoundError()
            disableNextButton()
            return
        }

        hideAllErrors()
        enableNextButton()
    }

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

    private fun showEmailDomainPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupBinding = PopupEmailBinding.inflate(inflater)

        popupBinding.root.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
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
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}