package com.example.planup.main.my.ui

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageDeleteAccountBinding
import com.example.planup.login.LoginActivityNew
import com.example.planup.main.my.adapter.CloseAccountAdapter
import com.example.planup.network.controller.UserController

class MypageDeleteAccountFragment: Fragment(), CloseAccountAdapter{
    lateinit var binding: FragmentMypageDeleteAccountBinding
    lateinit var name: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageDeleteAccountBinding.inflate(inflater,container,false)
        clickListener()

        /*사용자 이름 받아와야 함*/
        //binding.deleteAccountNameTv.text = name

        return binding.root
    }

    private fun clickListener() {

        /*뒤로가기*/
        binding.deleteAccountBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageOtherFragment())
                .commitAllowingStateLoss()
        }

        /*회원 탈퇴 체크박스*/
        binding.deleteAgreeCheckIv.setOnClickListener{
            binding.deleteAgreeCheckIv.visibility = View.GONE
            binding.deleteAgreeUncheckIv.visibility = View.VISIBLE
            binding.btnDeleteAccountTv.isActivated = false
        }
        binding.deleteAgreeUncheckIv.setOnClickListener{
            binding.deleteAgreeCheckIv.visibility = View.VISIBLE
            binding.deleteAgreeUncheckIv.visibility = View.GONE
            binding.btnDeleteAccountTv.isActivated = true
        }

        /*회원 탈퇴 버튼*/
        binding.btnDeleteAccountTv.setOnClickListener {
            if(!binding.btnDeleteAccountTv.isActivated) return@setOnClickListener
            val service = UserController()
            service.setCloseAccountAdapter(this)
            service.closeAccountService(binding.deleteAccountReasonEt.text.toString())
        }
    }

    override fun successCloseAccount() {
        val intent = Intent(context as MainActivity, LoginActivityNew::class.java)
        startActivity(intent)
    }

    override fun failCloseAccount(message: String) {
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