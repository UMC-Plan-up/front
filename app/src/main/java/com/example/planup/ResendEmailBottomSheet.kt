package com.example.planup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ResendEmailBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_resend_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resendOption = view.findViewById<TextView>(R.id.resendEmailOption)
        val kakaoOption = view.findViewById<TextView>(R.id.kakaoLoginOption)
        val cancelButton = view.findViewById<AppCompatButton>(R.id.cancelButton)

        // 이메일 다시 보내기
        resendOption.setOnClickListener {
            // TODO: 이메일 재전송 로직 추가
            dismiss() // bottomsheet 닫기
        }

        // 카카오 로그인 사용하기
        kakaoOption.setOnClickListener {
            // TODO: 카카오 소셜로그인 로직 연결
            dismiss()
        }

        // 취소 버튼 → bottomsheet 닫기
        cancelButton.setOnClickListener {
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
}