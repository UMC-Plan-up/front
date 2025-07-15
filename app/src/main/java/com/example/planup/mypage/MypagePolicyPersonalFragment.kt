package com.example.planup.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypagePolicyPersonalBinding

class MypagePolicyPersonalFragment: Fragment(){
    lateinit var binding: FragmentMypagePolicyPersonalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePolicyPersonalBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.policyPersonalBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageFragment())
                .commitAllowingStateLoss()
        }
    }
}