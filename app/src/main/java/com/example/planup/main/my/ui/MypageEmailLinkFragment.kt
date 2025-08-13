package com.example.planup.main.my.ui

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import androidx.core.graphics.drawable.toDrawable
import com.example.planup.databinding.FragmentMypageEmailLinkBinding
import com.example.planup.main.my.adapter.SignupLinkAdapter
import com.example.planup.network.controller.UserController

class MypageEmailLinkFragment:Fragment(),SignupLinkAdapter {

    lateinit var binding: FragmentMypageEmailLinkBinding
    lateinit var email: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageEmailLinkBinding.inflate(inflater,container,false)

        //이전 프레그먼트에서 전달받은 이메일
        email = arguments?.getString("email","not received").toString()
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.emailSecondBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageEmailCheckFragment())
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
                dialog.setCanceledOnTouchOutside(true)
            }
            //취소 버튼 클릭
            dialog.findViewById<View>(R.id.popup_cancel_btn).setOnClickListener{
                dialog.dismiss()
            }
            //이메일 재전송 클릭
            dialog.findViewById<View>(R.id.popup_email_send_again_tv).setOnClickListener{
                val emailResendService = UserController()
                emailResendService.setSignupLinkAdapter(this)
                emailResendService.signupRelinkService(email)
                dialog.dismiss()
            }
            //카카오 소셜 로그인 클릭
            dialog.findViewById<View>(R.id.popup_use_kakao).setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    override fun successEmailSend(email: String) {
        Log.d("okhttp",email)
    }

    override fun failEmailSend(message: String) {
        Log.d("okhttp",message)
    }
}