package com.example.planup.main.my.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageEmailSecondBinding
import androidx.core.graphics.drawable.toDrawable

class MypageEmailSecondFragment:Fragment() {

    lateinit var binding: FragmentMypageEmailSecondBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageEmailSecondBinding.inflate(inflater,container,false)

        clickListener()

        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.emailSecondBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageEmailFirstFragment())
                .commitAllowingStateLoss()
        }
        /*인증링크를 받지 못하였나요?*/
        binding.emailSecondNotReceiveTv.setOnClickListener{
            val dialog = Dialog(context as MainActivity)
            dialog.setContentView(R.layout.popup_email_not_receive)
            dialog.window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setGravity(Gravity.BOTTOM)
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                dialog.setCanceledOnTouchOutside(false)
            }
            dialog.findViewById<View>(R.id.btn_popup_cancel_tv).setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
    }
}