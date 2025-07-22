package com.example.planup.mypage

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypagePasswordSecondBinding
import androidx.core.graphics.drawable.toDrawable

class MypagePasswordSecondFragment: Fragment() {

    lateinit var binding: FragmentMypagePasswordSecondBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePasswordSecondBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.passwordSecondBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypagePasswordFirstFragment())
                .commitAllowingStateLoss()
        }
        /*임시 리스너
        * 원래는 이메일로 전달받은 링크를 통해 이동해야 함*/
        binding.passwordSecondExplainTv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypagePasswordThirdFragment())
                .commitAllowingStateLoss()
        }

        /*이메일을 받지 못하셨나요?*/
        binding.passwordSecondNotReceiveTv.setOnClickListener{
            val dialog = Dialog(context as MainActivity)
            dialog.setContentView(R.layout.popup_email_not_receive)
            dialog.window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                setGravity(Gravity.BOTTOM)
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                dialog.findViewById<View>(R.id.btn_popup_cancel_tv).setOnClickListener{
                    dialog.dismiss()
                }
                dialog.setCanceledOnTouchOutside(false)
            }
            dialog.show()
        }
    }
}