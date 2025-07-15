package com.example.planup.mypage

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageOtherBinding

class MypageOtherFragment:Fragment() {
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
                .replace(R.id.main_container,MypageFragment())
                .commitAllowingStateLoss()
        }

        /*로그아웃*/
        binding.otherLogOutCl.setOnClickListener{
            makePopup()
        }
        /*회원 탈퇴*/
        binding.otherDeleteAccountCl.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageDeleteAccountFragment())
                .commitAllowingStateLoss()
        }
    }
    private fun makePopup(){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_logout)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
        }

        /*취소 버튼 클릭 시 팝업 사라짐*/
        dialog.findViewById<View>(R.id.popup_logout_cancel_iv).setOnClickListener{
            dialog.dismiss()
        }

        /*확인 버튼 클릭 시 로그인 혹면으로 이동*/
        dialog.findViewById<View>(R.id.popup_logout_check_iv).setOnClickListener{
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container,LoginActivity())
//                .commitAllowingStateLoss()
        }
        dialog.show()
    }
}