package com.example.planup.toast

import android.content.Context
import android.content.res.Resources
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import com.example.planup.databinding.ToastIncorrectEmailBinding

object ToastIncorrectEmail {
    fun createToast(context: Context): Toast? {
        val inflater = LayoutInflater.from(context)
        val binding = ToastIncorrectEmailBinding.inflate(inflater)

        return Toast(context).apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER, 0, 16.toPx())
            duration = Toast.LENGTH_SHORT
            view = binding.root
        }
    }

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}