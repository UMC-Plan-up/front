package com.example.planup.main.my.ui

import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import androidx.core.graphics.drawable.toDrawable
import com.example.planup.databinding.FragmentMypageEmailLinkBinding
import com.example.planup.main.home.ui.HomeFragment
import com.example.planup.main.my.adapter.EmailLinkAdapter
import com.example.planup.main.my.adapter.SignupLinkAdapter
import com.example.planup.network.controller.UserController

class MypageEmailLinkFragment:Fragment(), EmailLinkAdapter {

    lateinit var binding: FragmentMypageEmailLinkBinding

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor

    lateinit var newEmail: String
    //딥링크를 통해 들어왔다면 이메일 변경 팝업 출력
    private var isDeepLink:Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageEmailLinkBinding.inflate(inflater,container,false)
        //이전 프레그먼트에서 전달받은 이메일
        init()
        clickListener()
        Log.d("okhttp",arguments?.getBoolean("deepLink")!!.toString())
        if (arguments?.getBoolean("deepLink")!!){
            showPopup(newEmail)
        }
        return binding.root
    }

    private fun init(){
        prefs = (context as MainActivity).getSharedPreferences("userInfo",MODE_PRIVATE)
        editor = prefs.edit()
        newEmail = prefs.getString("newEmail","no-email")!!
        binding.emailSecondExplainTv.text = getString(R.string.link_by_email,newEmail)
    }
    //클릭 이벤트 처리
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
                emailResendService.setEmailLinkAdapter(this)
                emailResendService.emailRelinkService(newEmail)
                dialog.dismiss()
            }
            //카카오 소셜 로그인 클릭
            dialog.findViewById<View>(R.id.popup_use_kakao).setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    //이메일 변경 성공한 경우 팝업 출력
    private fun showPopup(email: String){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_email_changed)
        editor.putString("email",email)
        editor.apply()
        //팝업 속성 정의
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
            setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.transparent))
            //외부 터치 불가
            dialog.setCanceledOnTouchOutside(false)
            //설명에 변경된 이메일 반영
            dialog.findViewById<TextView>(R.id.popup_email_sub_tv).text = getString(R.string.popup_email_explain,email)
        }
        //확인버튼 클릭 시 홈 화면으로 이동
        dialog.findViewById<TextView>(R.id.popup_email_reset_btn).setOnClickListener {
            dialog.dismiss()
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,HomeFragment())
                .commitAllowingStateLoss()
        }
        dialog.show()
    }


    //이메일 변경 성공 시 팝업 출력
    override fun successEmailLink(email: String) {
        //showPopup(email)
    }
    //이메일 변경 오류 시 토스트 메시지
    override fun failEmailLink(message: String) {
        Log.d("okhttp",message)
    }
}