package com.example.planup.main.my.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypagePolicyBinding

class MypagePolicyFragment:Fragment() {
    lateinit var binding: FragmentMypagePolicyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePolicyBinding.inflate(inflater,container,false)
        init()
        clickListener()
        return binding.root
    }

    private fun init(){
        binding.mypagePolicyCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val height = binding.mypagePolicyCl.height
                binding.mypagePolicyInnerCl.minHeight = height
                binding.mypagePolicyCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.policyBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
        /*서비스 이용 약관*/
        binding.policyServiceCl.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePolicyServiceFragment())
                .commitAllowingStateLoss()
        }
        /*개인정보 처리방침*/
        binding.policyPersonalCl.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePolicyPersonalFragment())
                .commitAllowingStateLoss()
        }
    }
}