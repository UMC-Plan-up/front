package com.example.planup.signup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.network.RetrofitInstance
import com.example.planup.signup.data.ResendEmailRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class ResendEmailBottomSheet(private val email: String) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_resend_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // "이메일 다시 보내기" 클릭 시 API 호출
        val resendOption = view.findViewById<TextView>(R.id.resendEmailOption)
        resendOption.setOnClickListener {
            resendEmail()
        }

        // [취소] 버튼 클릭 시 닫기
        val cancelButton = view.findViewById<TextView>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun resendEmail() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = RetrofitInstance.userApi.resendEmail(
                    ResendEmailRequest(email)
                )

                if (res.isSuccessful && res.body()?.isSuccess == true) {
                    // 성공 시 팝업 닫기
                    dismiss()
                } else {
                    // 실패 시 Toast 표시
                    Toast.makeText(
                        requireContext(),
                        res.body()?.message ?: "재발송 실패",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    companion object {
        fun newInstance(email: String): ResendEmailBottomSheet {
            return ResendEmailBottomSheet(email)
        }
    }
}
