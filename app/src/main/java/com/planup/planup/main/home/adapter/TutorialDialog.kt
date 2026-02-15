package com.planup.planup.main.home.adapter

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.planup.planup.R
import androidx.core.graphics.drawable.toDrawable
import android.widget.TextView

class TutorialDialog(
    private val layoutResId: Int,
    private val onNextClicked: (() -> Unit)? = null,
    private val textReplacements: Map<Int, String>? = null // TextView id -> 치환 값
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(layoutResId, container, false)

        textReplacements?.forEach { (id, value) ->
            val tv = view.findViewById<TextView>(id)
            tv?.text = String.format(tv.text.toString(), value)
        }

        // 다음 버튼 클릭 처리
        val btnNext = view.findViewById<View>(R.id.tutorial_next_iv)
        btnNext?.setOnClickListener {
            dismiss() // 현재 다이얼로그 닫기
            onNextClicked?.invoke() // 다음 단계로
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable()) // 배경 투명
        }
    }
}
