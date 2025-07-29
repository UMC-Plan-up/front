package com.example.planup.main.my.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageAlertBinding

class MypageAlertFragment : Fragment() {
    lateinit var binding: FragmentMypageAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageAlertBinding.inflate(inflater, container, false)
        setSpinner(binding.serviceTimeSp,R.array.challenge_alert_time)
        setSpinner(binding.serviceHourSp,R.array.challenge_alert_hour)
        setSpinner(binding.serviceMinuteSp,R.array.challenge_alert_minute)
        clickListener()
        return binding.root
    }

    private fun clickListener() {
        /*뒤로 가기*/
        binding.serviceBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, MypageFragment())
                .commitAllowingStateLoss()
        }

        /*푸시 알림 설정*/
        binding.servicePushOnIv.setOnClickListener {
            binding.servicePushOnIv.visibility = View.GONE
            binding.servicePushOffIv.visibility = View.VISIBLE
        }
        binding.servicePushOffIv.setOnClickListener {
            binding.servicePushOnIv.visibility = View.VISIBLE
            binding.servicePushOffIv.visibility = View.GONE
        }

        /*설정 목표 알림 설정*/
        binding.serviceReceiveAlertOnIv.setOnClickListener {
            binding.serviceReceiveAlertOnIv.visibility = View.GONE
            binding.serviceReceiveAlertOffIv.visibility = View.VISIBLE
        }
        binding.serviceReceiveAlertOffIv.setOnClickListener {
            binding.serviceReceiveAlertOnIv.visibility = View.VISIBLE
            binding.serviceReceiveAlertOffIv.visibility = View.GONE
        }

        /*정기 알림 설정*/
        binding.serviceRegularOnIv.setOnClickListener {
            binding.serviceRegularOnIv.visibility = View.GONE
            binding.serviceRegularOffIv.visibility = View.VISIBLE
            binding.serviceDayCl.visibility = View.GONE
        }
        binding.serviceRegularOffIv.setOnClickListener {
            binding.serviceRegularOnIv.visibility = View.VISIBLE
            binding.serviceRegularOffIv.visibility = View.GONE
            binding.serviceDayCl.visibility = View.VISIBLE
        }

        /* 시간 설정 */

        /* 요일 선택 효과 */
        val selected = ContextCompat.getColor(context, R.color.blue_200)
        val unselected = ContextCompat.getColor(context, R.color.black_300)
        binding.serviceEverydayCl.setOnClickListener { //매일
            binding.serviceEverydayCl.isActivated = !binding.serviceEverydayCl.isActivated
            binding.serviceEverydayTv.setTextColor(
                if (binding.serviceEverydayCl.isActivated) selected else unselected
            )
        }
        // 월요일
        binding.serviceMondayCl.setOnClickListener {
            binding.serviceMondayCl.isActivated = !binding.serviceMondayCl.isActivated
            binding.serviceMondayTv.setTextColor(
                if (binding.serviceMondayCl.isActivated) selected else unselected
            )
        }

        // 화요일
        binding.serviceTuesdayCl.setOnClickListener {
            binding.serviceTuesdayCl.isActivated = !binding.serviceTuesdayCl.isActivated
            binding.serviceTuesdayTv.setTextColor(
                if (binding.serviceTuesdayCl.isActivated) selected else unselected
            )
        }

        // 수요일
        binding.serviceWednesdayCl.setOnClickListener {
            binding.serviceWednesdayCl.isActivated = !binding.serviceWednesdayCl.isActivated
            binding.serviceWednesdayTv.setTextColor(
                if (binding.serviceWednesdayCl.isActivated) selected else unselected
            )
        }

        // 목요일
        binding.serviceThursdayCl.setOnClickListener {
            binding.serviceThursdayCl.isActivated = !binding.serviceThursdayCl.isActivated
            binding.serviceThursdayTv.setTextColor(
                if (binding.serviceThursdayCl.isActivated) selected else unselected
            )
        }

        // 금요일
        binding.serviceFridayCl.setOnClickListener {
            binding.serviceFridayCl.isActivated = !binding.serviceFridayCl.isActivated
            binding.serviceFridayTv.setTextColor(
                if (binding.serviceFridayCl.isActivated) selected else unselected
            )
        }

        // 토요일
        binding.serviceSaturdayCl.setOnClickListener {
            binding.serviceSaturdayCl.isActivated = !binding.serviceSaturdayCl.isActivated
            binding.serviceSaturdayTv.setTextColor(
                if (binding.serviceSaturdayCl.isActivated) selected else unselected
            )
        }

        // 일요일
        binding.serviceSundayCl.setOnClickListener {
            binding.serviceSundayCl.isActivated = !binding.serviceSundayCl.isActivated
            binding.serviceSundayTv.setTextColor(
                if (binding.serviceSundayCl.isActivated) selected else unselected
            )
        }


    }

    private fun setSpinner(spinnerId:AppCompatSpinner, stringId: Int){
        val spinner = spinnerId
        val items = resources.getStringArray(stringId)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_challenge_alert_spinner, items)//스피너 위젯 설정
        adapter.setDropDownViewResource(R.layout.dropdown_alert_ampm)//드롭다운 메뉴 설정
        spinner.adapter = adapter

        spinner.setSelection(0,false)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {}
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }
}