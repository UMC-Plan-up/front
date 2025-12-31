package com.example.planup.main.my.ui.dialog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.DialogFragment
import com.example.planup.R
import com.example.planup.databinding.PopupEmailChangedBinding
import com.example.planup.login.ui.LoginActivityNew

class EmailChangeSuccessDialog : DialogFragment() {

    companion object {
        private const val EMAIL = "email"

        fun getInstance(
            email: String
        ): EmailChangeSuccessDialog {
            return EmailChangeSuccessDialog().apply {
                arguments = Bundle().apply {
                    putString(EMAIL, email)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    private var _binding: PopupEmailChangedBinding? = null
    private val binding
        get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopupEmailChangedBinding.inflate(inflater, container, false)
        dialog?.window?.let { window ->
            window.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            window.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val email = arguments?.getString(EMAIL)
        binding.popupEmailSubTv.text = getString(R.string.popup_email_explain, email)
        binding.popupEmailResetBtn.setOnClickListener {
            startToLogin()
        }
    }

    private fun startToLogin() {
        val intent = Intent(requireContext(), LoginActivityNew::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}