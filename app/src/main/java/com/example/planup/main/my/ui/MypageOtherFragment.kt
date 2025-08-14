package com.example.planup.main.my.ui

import android.app.Dialog
import android.content.Intent
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
import com.example.planup.databinding.FragmentMypageOtherBinding
import androidx.core.graphics.drawable.toDrawable
import com.example.planup.login.LoginActivityNew
import com.example.planup.main.my.adapter.LogoutAdapter
import com.example.planup.network.controller.UserController

class MypageOtherFragment:Fragment(), LogoutAdapter {
    lateinit var binding:FragmentMypageOtherBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageOtherBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.otherBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }

        /*로그아웃*/
        binding.otherLogOutCl.setOnClickListener{
            makePopup()
        }
        /*회원 탈퇴*/
        binding.otherDeleteAccountCl.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageDeleteAccountFragment())
                .commitAllowingStateLoss()
        }
    }
    private fun makePopup(){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_logout)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)

            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }

        /*취소 버튼 클릭 시 팝업 사라짐*/
        dialog.findViewById<View>(R.id.btn_logout_no_tv).setOnClickListener{
            dialog.dismiss()
        }

        /*확인 버튼 클릭 시 로그인 화면으로 이동*/
        dialog.findViewById<View>(R.id.btn_logout_ok_tv).setOnClickListener{
            val logoutService = UserController()
            logoutService.setLogoutAdapter(this)
            logoutService.logoutService()
        }
        dialog.show()
    }

    override fun successLogout() {
        val intent = Intent(context as MainActivity, LoginActivityNew::class.java)
        startActivity(intent)
    }

    override fun failLogout(message: String) {
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