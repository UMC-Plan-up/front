package com.example.planup.main.goal.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentGoalBinding

class GoalFragment : Fragment() {
    lateinit var binding: FragmentGoalBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

//    private fun clickListener(){
////        // 알림
////        binding.challengeMainAlertIv.setOnClickListener{}
////        // 주석
////        binding.challengeMainTodoIv.setOnClickListener{}
////        // 함께 도전 중인 친구들
////        binding.challengeMainFriendTitleIv.setOnClickListener{}
//    }
//
//    /*챌린지 참여 요청 팝업*/
//    private fun makePopup(){
//        val dialog = Dialog(context as MainActivity)
//        dialog.setContentView(R.layout.popup_challenge)
//        dialog.window?.apply {
//            setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
//            setGravity(Gravity.BOTTOM)
//        }
//
//        dialog.findViewById<View>(R.id.popup_challenge_check_btn_iv).setOnClickListener{
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_container, GoalFragment())
//                .commitAllowingStateLoss()
//        }
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.show()
//    }

    private fun clickListener(){

    }

}