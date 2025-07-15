package com.example.planup.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageServiceBinding

class MypageServiceFragment:Fragment() {
    lateinit var binding: FragmentMypageServiceBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageServiceBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        /*뒤로 가기*/
        binding.serviceBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,MypageFragment())
                .commitAllowingStateLoss()
        }

        /*푸시 알림 설정*/
        binding.servicePushAllowOnIv.setOnClickListener{
            binding.servicePushAllowOnIv.visibility = View.GONE
            binding.servicePushAllowOffIv.visibility = View.VISIBLE
        }
        binding.servicePushAllowOffIv.setOnClickListener{
            binding.servicePushAllowOnIv.visibility = View.VISIBLE
            binding.servicePushAllowOffIv.visibility = View.GONE
        }

        /*설정 목표 알림 설정*/
        binding.serviceReceiveAlertOnIv.setOnClickListener{
            binding.serviceReceiveAlertOnIv.visibility = View.GONE
            binding.serviceReceiveAlertOffIv.visibility = View.VISIBLE
        }
        binding.serviceReceiveAlertOffIv.setOnClickListener{
            binding.serviceReceiveAlertOnIv.visibility = View.VISIBLE
            binding.serviceReceiveAlertOffIv.visibility = View.GONE
        }

        /*정기 알림 설정*/
        binding.serviceRegularAlertOnIv.setOnClickListener{
            binding.serviceRegularAlertOnIv.visibility = View.GONE
            binding.serviceRegularAlertOffIv.visibility = View.VISIBLE
            binding.serviceSetTimeCl.visibility = View.GONE
        }
        binding.serviceRegularAlertOffIv.setOnClickListener{
            binding.serviceRegularAlertOnIv.visibility = View.VISIBLE
            binding.serviceRegularAlertOffIv.visibility = View.GONE
            binding.serviceSetTimeCl.visibility = View.VISIBLE
        }
        /*요일별 선택 효과 구현 필요*/
    }
}