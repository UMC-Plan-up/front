package com.example.planup.signup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.planup.databinding.PopupResendEmailBinding
import com.example.planup.network.RetrofitInstance
import com.example.planup.password.data.PasswordChangeEmailRequestDto
import com.example.planup.signup.data.ResendEmailRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ResendEmailBottomsheet : BottomSheetDialogFragment() {

    private var _binding: PopupResendEmailBinding? = null
    private val binding get() = _binding!!

    private var isSending = false
    private lateinit var email: String
    private lateinit var mode: ResendMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString(ARG_EMAIL) ?: ""
        mode  = arguments?.getSerializable(ARG_MODE) as? ResendMode ?: ResendMode.SIGNUP
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PopupResendEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.resendEmailOption.setOnClickListener {
            if (!isSending) resendEmail()
        }

        binding.cancelButton.setOnClickListener { dismiss() }
    }

    private fun resendEmail() {
        if (email.isBlank()) {
            Toast.makeText(requireContext(), "이메일 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        isSending = true
        binding.resendEmailOption.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                when (mode) {
                    /* 회원가입 메일 재발송 */
                    ResendMode.SIGNUP -> {
                        val res = RetrofitInstance.userApi.resendEmail(
                            ResendEmailRequest(email)
                        )
                        val ok = res.isSuccessful && res.body()?.isSuccess == true
                        if (ok) {
                            dismiss()
                        } else {
                            val msg = res.body()?.message ?: res.errorBody()?.string() ?: "재발송 실패"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                            binding.resendEmailOption.isEnabled = true
                        }
                    }

                    /* 비밀번호 변경 확인 메일 재발송 */
                    ResendMode.PASSWORD -> {
                        val res = RetrofitInstance.userApi.resendPasswordChangeEmail(
                            PasswordChangeEmailRequestDto(email)
                        )
                        val ok = res.isSuccessful && res.body()?.isSuccess == true
                        if (ok) {
                            dismiss()
                        } else {
                            val msg = res.body()?.message ?: res.errorBody()?.string() ?: "재발송 실패"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                            binding.resendEmailOption.isEnabled = true
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.resendEmailOption.isEnabled = true
            } finally {
                isSending = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_EMAIL = "email"
        private const val ARG_MODE  = "mode"

        fun newInstance(email: String, mode: ResendMode = ResendMode.SIGNUP): com.example.planup.signup.ui.ResendEmailBottomsheet {
            return ResendEmailBottomsheet().apply {
                arguments = Bundle().apply {
                    putString(ARG_EMAIL, email)
                    putSerializable(ARG_MODE, mode)
                }
            }
        }
    }
}

