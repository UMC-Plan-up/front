package com.example.planup.main.my.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageKakaoBinding
import com.example.planup.main.my.adapter.KakaoAdapter
import com.example.planup.network.controller.UserController

class MypageKakaoFragment:Fragment(), KakaoAdapter {

    lateinit var binding:FragmentMypageKakaoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageKakaoBinding.inflate(inflater,container,false)
        init()
        clickListener()
        return binding.root
    }

    private fun init(){
        val service = UserController()
        service.setKakaoAdapter(this)
        service.kakaoService()
    }
    private fun clickListener() {
        /*뒤로 가기*/
        binding.kakaoBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
    }

    override fun successKakao(kakaoAddr: String) {
        //연동된 카카오 이메일 계정 전달
        binding.kakaoTitleTv.text = resources.getText(R.string.kakao_sync,kakaoAddr)
    }

    override fun failKakao() {}
}