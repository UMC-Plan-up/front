package com.example.planup.main.my.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypagePolicyServiceBinding

class MypagePolicyServiceFragment: Fragment(){
    lateinit var binding: FragmentMypagePolicyServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypagePolicyServiceBinding.inflate(inflater,container,false)
        init()
        clickListener()
        return binding.root
    }
    private fun init(){
        binding.mypagePolicyServiceCl.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                val height = binding.mypagePolicyServiceCl.height
                binding.mypagePolicyServiceInnerCl.minHeight = height
                binding.mypagePolicyServiceCl.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })
    }
    private fun clickListener(){
        /*뒤로 가기*/
        binding.policyServiceBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypagePolicyFragment())
                .commitAllowingStateLoss()
        }
    }
}