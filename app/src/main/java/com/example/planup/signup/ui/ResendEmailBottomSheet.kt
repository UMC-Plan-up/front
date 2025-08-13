package com.example.planup.signup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.PopupResendEmailBinding
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.data.ResendEmailRequest
import com.example.planup.signup.data.ResendEmailResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import retrofit2.Response

class ResendEmailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: PopupResendEmailBinding? = null
    private val binding get() = _binding!!

    private var isSending = false
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString(ARG_EMAIL) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopupResendEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "이메일 다시 보내기" 클릭 시 API 호출
        binding.resendEmailOption.setOnClickListener {
            if (!isSending) {
                resendEmail(it)
            }
        }

        // [취소] 버튼 클릭 시 닫기
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun resendEmail(trigger: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            isSending = true
            trigger.isEnabled = false
            try {
                val res: Response<ResendEmailResponse> =
                    RetrofitInstance.userApi.resendEmail(
                        ResendEmailRequest(email)
                    )

                if (res.isSuccessful && res.body()?.isSuccess == true) {
                    Toast.makeText(requireContext(), "인증 메일을 재발송했어요.", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    val msg = res.body()?.message ?: res.errorBody()?.string() ?: "재발송 실패"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isSending = false
                trigger.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_EMAIL = "email"

        fun newInstance(email: String): ResendEmailBottomSheet {
            val fragment = ResendEmailBottomSheet()
            val args = Bundle()
            args.putString(ARG_EMAIL, email)
            fragment.arguments = args
            return fragment
        }
    }
}
