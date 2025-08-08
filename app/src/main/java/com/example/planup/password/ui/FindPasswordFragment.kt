package com.example.planup.password.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.login.ui.LoginActivity
import com.example.planup.password.ResetPasswordActivity

class FindPasswordFragment : Fragment(R.layout.fragment_find_password) {

    private lateinit var emailEditText: EditText
    private lateinit var emailFormatErrorText: TextView
    private lateinit var emailNotFoundErrorText: TextView
    private lateinit var nextButton: AppCompatButton
    private lateinit var emailDropdownIcon: ImageView

    private val registeredEmails = listOf("user@gmail.com")

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val baseMargin = 33.dp()
        val gapFromKeyboard = 25.dp()
        val nextBtn = nextButton

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            val targetMargin = if (imeVisible) imeBottom + gapFromKeyboard else baseMargin

            nextBtn.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = targetMargin
            }
            insets
        }

        emailEditText = view.findViewById(R.id.emailEditText)
        emailFormatErrorText = view.findViewById(R.id.emailFormatErrorText)
        emailNotFoundErrorText = view.findViewById(R.id.emailNotFoundErrorText)
        nextButton = view.findViewById(R.id.nextButton)
        emailDropdownIcon = view.findViewById(R.id.emailDropdownIcon)

        val backIcon = view.findViewById<ImageView>(R.id.backIcon)

        disableNextButton()
        hideAllErrors()

        emailEditText.addTextChangedListener {
            val email = it.toString().trim()
            validateEmail(email)
        }

        nextButton.setOnClickListener {
            if (nextButton.isEnabled) {
                (requireActivity() as ResetPasswordActivity)
                    .navigateToFragment(FindPasswordEmailSentFragment())
            }
        }

        backIcon.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        emailDropdownIcon.setOnClickListener {
            showEmailDomainPopup()
        }

        // EditText 외부 터치 시 키보드 숨기기
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && emailEditText.isFocused) {
                emailEditText.clearFocus()
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
        emailFormatErrorText.visibility = View.VISIBLE
        emailNotFoundErrorText.visibility = View.GONE
    }

    private fun showEmailNotFoundError() {
        emailFormatErrorText.visibility = View.GONE
        emailNotFoundErrorText.visibility = View.VISIBLE
    }

    private fun hideAllErrors() {
        emailFormatErrorText.visibility = View.GONE
        emailNotFoundErrorText.visibility = View.GONE
    }

    private fun enableNextButton() {
        nextButton.isEnabled = true
        nextButton.backgroundTintList =
            requireContext().getColorStateList(R.color.blue_200)
    }

    private fun disableNextButton() {
        nextButton.isEnabled = false
        nextButton.backgroundTintList =
            requireContext().getColorStateList(R.color.black_200)
    }

    private fun openNextStep() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.resetPasswordContainer, FindPasswordEmailSentFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showEmailDomainPopup() {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_email, null)

        popupView.measure(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        )
        val popupWidth = popupView.measuredWidth

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val domainGmail = popupView.findViewById<TextView>(R.id.domainGmail)
        val domainNaver = popupView.findViewById<TextView>(R.id.domainNaver)
        val domainKakao = popupView.findViewById<TextView>(R.id.domainKakao)

        val addDomain: (String) -> Unit = { domain ->
            val currentText = emailEditText.text.toString()
            val updatedText = if (currentText.contains("@")) {
                currentText.substringBefore("@") + "@$domain"
            } else {
                "$currentText@$domain"
            }
            emailEditText.setText(updatedText)
            emailEditText.setSelection(updatedText.length)
            popupWindow.dismiss()
        }

        domainGmail.setOnClickListener { addDomain("gmail.com") }
        domainNaver.setOnClickListener { addDomain("naver.com") }
        domainKakao.setOnClickListener { addDomain("kakao.com") }

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.elevation = 8f

        val offsetX = emailEditText.width - popupWidth
        popupWindow.showAsDropDown(emailEditText, offsetX, 0)
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}