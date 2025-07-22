package com.example.planup.home

import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.HomeFragment
import com.example.planup.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentSettingAlertBinding
import androidx.core.graphics.drawable.toDrawable

class SettingAlertFragment:Fragment() {
    lateinit var binding: FragmentSettingAlertBinding
    var isFirst = true
    var isPushed:Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingAlertBinding.inflate(inflater,container,false)
        //isFirst()
        clickListener()
        return binding.root
    }

    private fun clickListener(){

        //임시 팝업
        binding.alertAlarmIv.setOnClickListener{
            makePopup()
        }

        //뒤로 가기
        binding.alertBackIv.setOnClickListener{
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, HomeFragment())
                .commitAllowingStateLoss()
        }

        //목표 알림받기 토글
        binding.alertReceiveOnIv.setOnClickListener{
            binding.alertReceiveOnIv.visibility = View.GONE
            binding.alertReceiveOffIv.visibility = View.VISIBLE
        }
        binding.alertReceiveOffIv.setOnClickListener{
            binding.alertReceiveOnIv.visibility = View.VISIBLE
            binding.alertReceiveOffIv.visibility = View.GONE
        }

        // 정기 알림 설정 토글
        binding.alertRegularOnIv.setOnClickListener{
            binding.alertRegularOnIv.visibility = View.GONE
            binding.alertReceiveOffIv.visibility = View.VISIBLE
        }
        binding.alertRegularOffIv.setOnClickListener{
            binding.alertRegularOnIv.visibility = View.VISIBLE
            binding.alertReceiveOffIv.visibility = View.GONE
        }

        // 요일 선택 효과
        val selected = ContextCompat.getColor(context, R.color.blue_200)
        val unselected = ContextCompat.getColor(context, R.color.black_300)

        // 매일
        binding.alertEverydayCl.setOnClickListener {
            binding.alertEverydayCl.isActivated = !binding.alertEverydayCl.isActivated
            binding.alertEverydayTv.setTextColor(
                if (binding.alertEverydayCl.isActivated) selected else unselected
            )
        }

        // 월요일
        binding.alertMondayCl.setOnClickListener {
            binding.alertMondayCl.isActivated = !binding.alertMondayCl.isActivated
            binding.alertMondayTv.setTextColor(
                if (binding.alertMondayCl.isActivated) selected else unselected
            )
        }

        // 화요일
        binding.alertTuesdayCl.setOnClickListener {
            binding.alertTuesdayCl.isActivated = !binding.alertTuesdayCl.isActivated
            binding.alertTuesdayTv.setTextColor(
                if (binding.alertTuesdayCl.isActivated) selected else unselected
            )
        }

        // 수요일
        binding.alertWednesdayCl.setOnClickListener {
            binding.alertWednesdayCl.isActivated = !binding.alertWednesdayCl.isActivated
            binding.alertWednesdayTv.setTextColor(
                if (binding.alertWednesdayCl.isActivated) selected else unselected
            )
        }

        // 목요일
        binding.alertThursdayCl.setOnClickListener {
            binding.alertThursdayCl.isActivated = !binding.alertThursdayCl.isActivated
            binding.alertThursdayTv.setTextColor(
                if (binding.alertThursdayCl.isActivated) selected else unselected
            )
        }

        // 금요일
        binding.alertFridayCl.setOnClickListener {
            binding.alertFridayCl.isActivated = !binding.alertFridayCl.isActivated
            binding.alertFridayTv.setTextColor(
                if (binding.alertFridayCl.isActivated) selected else unselected
            )
        }

        // 토요일
        binding.alertSaturdayCl.setOnClickListener {
            binding.alertSaturdayCl.isActivated = !binding.alertSaturdayCl.isActivated
            binding.alertSaturdayTv.setTextColor(
                if (binding.alertSaturdayCl.isActivated) selected else unselected
            )
        }

        // 일요일
        binding.alertSundayCl.setOnClickListener {
            binding.alertSundayCl.isActivated = !binding.alertSundayCl.isActivated
            binding.alertSundayTv.setTextColor(
                if (binding.alertSundayCl.isActivated) selected else unselected
            )
        }
    }
    private fun makePopup(){
        val dialog = Dialog(context as MainActivity)
        dialog.setContentView(R.layout.popup_push_alert)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
        dialog.setCanceledOnTouchOutside(false)

        //푸시 알림 받음
        dialog.findViewById<TextView>(R.id.popup_push_yes_btn).setOnClickListener{
            isFirst = false
            isPushed = true
            dialog.dismiss()
        }
        //푸시 알림 받지 않음
        dialog.findViewById<TextView>(R.id.popup_push_no_btn).setOnClickListener{
            isPushed = false
            dialog.dismiss()
            if (isFirst){ //첫 방문인 경우 온보딩 화면으로 이동
                isFirst = false
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, HomeFragment()) //온보딩 화면으로 수정해야 함
                    .commitAllowingStateLoss()
            } else{ //첫 방문이 아닌 경우 홈 화면으로 이동
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, HomeFragment())
                    .commitAllowingStateLoss()
            }
        }
        dialog.show()
    }

    //푸시 알림 설정 페이지에 처음 방문했는지 확인
//    private fun isFirst(){
//        var sharedPreference = SharedPreferences.OnSharedPreferenceChangeListener()
//    }
}