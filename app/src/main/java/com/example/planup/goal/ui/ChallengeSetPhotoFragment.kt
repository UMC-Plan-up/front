package com.example.planup.goal.ui

import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetPhotoBinding
import com.example.planup.goal.GoalActivity

class ChallengeSetPhotoFragment:Fragment() {
    private lateinit var binding: FragmentChallengeSetPhotoBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetPhotoBinding.inflate(inflater,container,false)
        init()
        clickListener()
        return binding.root
    }

    private fun init(){
        prefs = (context as GoalActivity).getSharedPreferences("challenge",MODE_PRIVATE)
        editor = prefs.edit()
    }
    private fun clickListener(){
        binding.challengePhotoBackIv.setOnClickListener {
            (context as GoalActivity).supportFragmentManager
                .popBackStack()
        }
        binding.challengePhotoNumberIv.setOnClickListener {
            dropdown(binding.challengePhotoNumberTv)
        }
        binding.challengePhotoNextBtn.setOnClickListener {
            if (!binding.challengePhotoNextBtn.isActivated) return@setOnClickListener
            editor.apply()
            (context as GoalActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.goal_container,ChallengeSetFrequencyFragment())
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
    private fun dropdown(view: View){
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_challenge_photo,null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(view)
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.transparent))
        popupView.findViewById<TextView>(R.id.popup_challenge_photo_once_tv).setOnClickListener {
            binding.challengePhotoNumberTv.setText(R.string.challenge_photo_once)
            editor.putInt("number",1)
            binding.challengePhotoNextBtn.isActivated = true
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.popup_challenge_photo_twice_tv).setOnClickListener {
            binding.challengePhotoNumberTv.setText(R.string.challenge_photo_twice)
            editor.putInt("number",2)
            binding.challengePhotoNextBtn.isActivated = true
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.popup_challenge_photo_three_tv).setOnClickListener {
            binding.challengePhotoNumberTv.setText(R.string.challenge_photo_three)
            editor.putInt("number",3)
            binding.challengePhotoNextBtn.isActivated = true
            popupWindow.dismiss()
        }
    }
}