package com.example.planup.main.goal.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.planup.R
import android.widget.ImageView
import android.widget.Button
import androidx.core.graphics.drawable.toDrawable

class GoalUpdateDialog : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃 불러오기
        val view = inflater.inflate(R.layout.dialog_goal_update, container, false)

        // 닫기 버튼 클릭 이벤트
        view.findViewById<ImageView>(R.id.goal_update_close_btn).setOnClickListener {
            dismiss()
        }

        // 확인 버튼 클릭 이벤트
        view.findViewById<Button>(R.id.goal_update_check_btn).setOnClickListener {
            // TODO: 세부 페이지로 이동 로직 작성
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // 다이얼로그를 전체 화면으로 확장
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // 배경을 투명하게 (FrameLayout 배경색으로 덮음)
        dialog?.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
    }
}