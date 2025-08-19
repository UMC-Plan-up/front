package com.example.planup.main.my.ui

import android.app.Dialog
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
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import androidx.core.graphics.drawable.toDrawable
import com.example.planup.databinding.FragmentMypagePasswordLinkBinding
import com.example.planup.main.my.adapter.PasswordLinkAdapter
import com.example.planup.network.controller.UserController
import com.example.planup.network.data.PasswordLink

class MypagePasswordLinkFragment: Fragment(), PasswordLinkAdapter {

    lateinit var binding: FragmentMypagePasswordLinkBinding
    lateinit var email: String

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePasswordLinkBinding.inflate(inflater,container,false)
        init()
        clickListener()
        return binding.root
    }

    private fun init(){
        prefs = (context as MainActivity).getSharedPreferences("userInfo",MODE_PRIVATE)
        editor = prefs.edit()
        email = prefs.getString("email","email").toString()
        binding.passwordSecondExplainTv.text = getString(R.string.link_by_email,email)
    }
    private fun clickListener(){
        /*뒤로 가기*/
        binding.passwordSecondBackIv.setOnClickListener{
            (context as MainActivity).navigateToFragment(MypagePasswordEmailFragment())
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
                    dialog.dismiss()
                    val service = UserController()
                    service.setPasswordLinkAdapter(MypagePasswordLinkFragment())
                    service.passwordRelinkService(email)
                }
                //카카오 로그인 클릭
                dialog.findViewById<View>(R.id.popup_cancel_btn).setOnClickListener{
                    dialog.dismiss()
                }
                dialog.setCanceledOnTouchOutside(false)
            }
            dialog.show()
        }
    }

    override fun successPasswordLink(email: String) {
        Log.d("okhttp",email)
    }
    override fun failPasswordLink(message: String) {
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = message

        val toast = Toast(context)
        toast.view = layout
        toast.duration = LENGTH_SHORT
        toast.setGravity(Gravity.BOTTOM,0,300)
        toast.show()
    }
}