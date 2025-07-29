package com.example.planup.goal.ui
/*1:1 챌린지 설정 플로우 챌린지 목표 설정하기
*목표명 및 1회 분량 입력하는 페이지
*/
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentChallengeSettingBinding

class ChallengeSettingFragment:Fragment() {
    lateinit var binding: FragmentChallengeSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSettingBinding.inflate(inflater,container,false)
        return binding.root
    }
}