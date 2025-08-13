package com.example.planup.password.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.planup.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.planup.databinding.PopupResendEmailBinding

class ResendEmailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: PopupResendEmailBinding? = null
    private val binding get() = _binding!!

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

        // 이메일 다시 보내기
        binding.resendEmailOption.setOnClickListener {
            // TODO: 이메일 재전송 로직 추가
            dismiss() // bottomsheet 닫기
        }

        // 카카오 로그인 사용하기
        binding.kakaoLoginOption.setOnClickListener {
            // TODO: 카카오 소셜로그인 로직 연결
            dismiss()
        }

        // 취소 버튼 → bottomsheet 닫기
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener { dialog ->

                val bottomSheet =
                    (dialog as com.google.android.material.bottomsheet.BottomSheetDialog)
                        .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}