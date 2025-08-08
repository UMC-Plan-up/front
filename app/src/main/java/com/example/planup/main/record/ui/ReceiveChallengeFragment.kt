package com.example.planup.main.record.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeReceivedBinding
import com.example.planup.main.MainActivity
import com.example.planup.main.home.ui.HomeFragment

class ReceiveChallengeFragment:Fragment() {
    lateinit var binding: FragmentChallengeReceivedBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeReceivedBinding.inflate(inflater,container,false)
        clickListener()
        return binding.root
    }

    private fun clickListener(){
        //뒤로가기: 홈 프레그먼트로 이동
        binding.backIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,HomeFragment())
                .commitAllowingStateLoss()
        }
        //챌린지 차며 거절 버튼: 토스트 메시지 출력 후 홈 프레그먼트로 이동
        binding.btnRejectTv.setOnClickListener {
            makeToast(R.string.toast_challenge_reject)
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,HomeFragment())
                .commitAllowingStateLoss()
        }
        binding.btnAcceptTv.setOnClickListener {
            makeToast(R.string.toast_challenge_accept)
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,HomeFragment())
                .commitAllowingStateLoss()
        }
        binding.btnAskNewPenaltyTv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container,AskNewPenaltyFragment())
                .commitAllowingStateLoss()
        }
    }
    private fun makeToast(textId:Int){
        val inflater = LayoutInflater.from(context)
        val layout = inflater.inflate(R.layout.toast_grey_template,null)
        layout.findViewById<TextView>(R.id.toast_grey_template_tv).text = getString(textId)

        val toast = Toast(context)
        toast.duration = LENGTH_SHORT
        toast.view = layout
        toast.setGravity(Gravity.BOTTOM,0,172)
        toast.show()
    }
}