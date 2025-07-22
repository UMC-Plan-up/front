package com.example.planup.main.my.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageKakaoBinding

class MypageKakaoFragment:Fragment() {

    lateinit var binding:FragmentMypageKakaoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageKakaoBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }

    private fun clickListener() {
        /*뒤로 가기*/
        binding.kakaoBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }
    }
}