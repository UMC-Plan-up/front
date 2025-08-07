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
import com.example.planup.databinding.FragmentMypagePasswordLinkBinding
import com.example.planup.network.controller.UserController

class MypagePasswordLinkFragment: Fragment(),ResponseViewer {

    lateinit var binding: FragmentMypagePasswordLinkBinding
    lateinit var email: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePasswordLinkBinding.inflate(inflater,container,false)
        email = savedInstanceState?.getString("email",null)!!
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.passwordSecondBackIv.setOnClickListener{
            (context as MainActivity).navigateFragment(MypagePasswordEmailFragment())
        }
        /*임시 리스너
        * 원래는 이메일로 전달받은 링크를 통해 이동해야 함*/
        binding.passwordSecondExplainTv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePasswordChangeFragment())
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
                //취소 버튼 클릭: 팝업 종료
                dialog.findViewById<View>(R.id.popup_cancel_btn).setOnClickListener{
                    dialog.dismiss()
                }
                //이메일 다시 받기 클릭
                dialog.findViewById<View>(R.id.popup_cancel_btn).setOnClickListener{
                    val emailService = UserController()
                    emailService.setResponseViewer(this@MypagePasswordLinkFragment)
                    emailService.emailService(0, email)
                }
                //카카오 로그인 클릭
                dialog.findViewById<View>(R.id.popup_cancel_btn).setOnClickListener{

                }
                dialog.setCanceledOnTouchOutside(false)
            }
            dialog.show()
        }
    }

    //UI 이벤트 없음
    override fun onResponseSuccess() {}
    override fun onResponseError(code: String, message: String ) {
        //디버깅
        Log.d("okhttp", "code: ${code}\nmessage: ${message}")
    }
}