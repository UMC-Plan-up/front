package com.example.planup.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentAlertBinding
import com.google.android.material.tabs.TabLayoutMediator

class AlertFragment : Fragment() {
    lateinit var binding: FragmentAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlertBinding.inflate(inflater, container, false)

        val category: Array<String> = resources.getStringArray(R.array.challenge)

        val adapter = AlertVPAdapter(this)
        binding.friendAlertVp.adapter = adapter
        TabLayoutMediator(binding.friendAlertTl, binding.friendAlertVp) { tab, position ->
            tab.text = category[position]  //포지션에 따른 텍스트
        }.attach()  //탭레이아웃과 뷰페이저를 붙여주는 기능


        return binding.root
    }
}