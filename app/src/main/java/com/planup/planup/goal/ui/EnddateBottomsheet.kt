package com.planup.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.planup.planup.R
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
