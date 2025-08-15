package com.example.planup.goal.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.planup.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.planup.databinding.PopupPushAlertSettingBinding

class PushAlertBottomSheet(
    context: Context
) : BottomSheetDialog(context, R.style.BottomSheetDialogTheme) {

    private lateinit var binding: PopupPushAlertSettingBinding

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

        binding = PopupPushAlertSettingBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        window?.setDimAmount(0.4f)

        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.noButton.setOnClickListener {
            cancelClickListener?.invoke()
            dismiss()
        }

        binding.yesButton.setOnClickListener {
            confirmClickListener?.invoke()
            dismiss()
        }
    }
}
