package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.planup.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EndDateBottomSheet : BottomSheetDialogFragment() {

    var onNoClicked: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_end_date, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                systemBars.bottom
            )
            insets
        }
        view.findViewById<TextView>(R.id.popup_push_yes_btn).setOnClickListener {
            // '네' 버튼을 클릭
            dismiss()
        }

        view.findViewById<TextView>(R.id.popup_push_no_btn).setOnClickListener {
            // '아니오' 버튼 클릭
            onNoClicked?.invoke()
            dismiss()
        }
    }
}
