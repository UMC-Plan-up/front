package com.example.planup.goal.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.planup.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.Button

class PushAlertPopupDialog(
    context: Context
) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {

    private var confirmClickListener: (() -> Unit)? = null
    private var cancelClickListener: (() -> Unit)? = null

    fun setOnConfirmClickListener(listener: () -> Unit) {
        confirmClickListener = listener
    }

    fun setOnCancelClickListener(listener: () -> Unit) {
        cancelClickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(context)
            .inflate(R.layout.popup_push_alert_setting, null)
        setContentView(view)

        window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        window?.setDimAmount(0.4f)

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val btnNo = view.findViewById<Button>(R.id.noButton)
        val btnYes = view.findViewById<Button>(R.id.yesButton)

        btnNo?.setOnClickListener {
            cancelClickListener?.invoke()
            dismiss()
        }

        btnYes?.setOnClickListener {
            confirmClickListener?.invoke()
            dismiss()
        }
    }
}
