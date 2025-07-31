package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetAlertBinding
import com.example.planup.goal.adapter.AlertRVAdapter
import com.example.planup.main.MainActivity
import com.example.planup.main.home.ui.HomeFragment

class ChallengeSetAlertFragment : Fragment() {
    lateinit var binding: FragmentChallengeSetAlertBinding
    private var isFirst = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeSetAlertBinding.inflate(inflater, container, false)
        clickListener()
        return binding.root
    }

    //클릭 이벤트
    private fun clickListener() {
        //알림받기 토글
        binding.alertReceiveOnIv.setOnClickListener {
            binding.alertReceiveOnIv.visibility = View.GONE
            binding.alertReceiveOffIv.visibility = View.VISIBLE
        }
        binding.alertReceiveOffIv.setOnClickListener {
            binding.alertReceiveOnIv.visibility = View.VISIBLE
            binding.alertReceiveOffIv.visibility = View.GONE
        }
        //정기알림 토글
        binding.alertRegularOnIv.setOnClickListener {
            binding.alertRegularOnIv.visibility = View.GONE
            binding.alertRegularOffIv.visibility = View.VISIBLE
        }
        binding.alertRegularOffIv.setOnClickListener {
            binding.alertRegularOnIv.visibility = View.VISIBLE
            binding.alertRegularOffIv.visibility = View.GONE
        }
        //저장 버튼 클릭
        binding.alertSaveBtn.setOnClickListener {
            if (isFirst) {//첫 방문인 경우 온보딩 페이지로 이동
                isFirst = false
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, HomeFragment())
                    .commitAllowingStateLoss()
            } else {//첫 방문이 아닌 경우 홈 페이지로 이동
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, HomeFragment())
                    .commitAllowingStateLoss()
            }
        }

        //정기 알림 시간 설정
        binding.alertTimeTv.setOnClickListener {
            binding.alertTimeCl.visibility = View.VISIBLE
        }
        binding.alertMorningTv.setOnClickListener {
            binding.alertTimeTv.setText(R.string.morning)
            binding.alertTimeCl.visibility = View.GONE
        }
        binding.alertAfternoonTv.setOnClickListener {
            binding.alertTimeTv.setText(R.string.afternoon)
            binding.alertTimeCl.visibility = View.GONE
        }
        //시각 설정
        binding.alertHourTv.setOnClickListener {
            binding.alertHourRv.visibility = View.VISIBLE
        }
        //분 설정
        binding.alertMinuteTv.setOnClickListener {
            binding.alertMinuteRv.visibility = View.VISIBLE
        }
        //정기 알림 요일 설정
        val selected = ContextCompat.getColor(context, R.color.blue_200)
        val unselected = ContextCompat.getColor(context, R.color.black_300)

        // 매일
        binding.alertEverydayTv.setOnClickListener {
            binding.alertEverydayTv.isSelected = !binding.alertEverydayTv.isSelected
            binding.alertEverydayTv.setTextColor(
                if (binding.alertEverydayTv.isSelected) selected else unselected
            )
        }

        // 월요일
        binding.alertMondayTv.setOnClickListener {
            binding.alertMondayTv.isSelected = !binding.alertMondayTv.isSelected
            binding.alertMondayTv.setTextColor(
                if (binding.alertMondayTv.isSelected) selected else unselected
            )
        }

        // 화요일
        binding.alertTuesdayTv.setOnClickListener {
            binding.alertTuesdayTv.isSelected = !binding.alertTuesdayTv.isSelected
            binding.alertTuesdayTv.setTextColor(
                if (binding.alertTuesdayTv.isSelected) selected else unselected
            )
        }

        // 수요일
        binding.alertWednesdayTv.setOnClickListener {
            binding.alertWednesdayTv.isSelected = !binding.alertWednesdayTv.isSelected
            binding.alertWednesdayTv.setTextColor(
                if (binding.alertWednesdayTv.isSelected) selected else unselected
            )
        }

        // 목요일
        binding.alertThursdayTv.setOnClickListener {
            binding.alertThursdayTv.isSelected = !binding.alertThursdayTv.isSelected
            binding.alertThursdayTv.setTextColor(
                if (binding.alertThursdayTv.isSelected) selected else unselected
            )
        }

        // 금요일
        binding.alertFridayTv.setOnClickListener {
            binding.alertFridayTv.isSelected = !binding.alertFridayTv.isSelected
            binding.alertFridayTv.setTextColor(
                if (binding.alertFridayTv.isSelected) selected else unselected
            )
        }

        // 토요일
        binding.alertSaturdayTv.setOnClickListener {
            binding.alertSaturdayTv.isSelected = !binding.alertSaturdayTv.isSelected
            binding.alertSaturdayTv.setTextColor(
                if (binding.alertSaturdayTv.isSelected) selected else unselected
            )
        }

        // 일요일
        binding.alertSundayTv.setOnClickListener {
            binding.alertSundayTv.isSelected = !binding.alertSundayTv.isSelected
            binding.alertSundayTv.setTextColor(
                if (binding.alertSundayTv.isSelected) selected else unselected
            )
        }


    }
    //알림설정 시간 드롭다운
    private fun showDropdown(){
        val hours = resources.getStringArray(R.array.spinner_hour).toCollection(ArrayList<String>())
        val minutes = resources.getStringArray(R.array.spinner_minute_second).toCollection(ArrayList<String>())
        val adapter = ArrayList<AlertRVAdapter>()
        adapter.add(0,AlertRVAdapter(hours))
        adapter.add(1, AlertRVAdapter(minutes))

        binding.alertHourRv.adapter = adapter[0]
        binding.alertMinuteRv.adapter = adapter[1]

        adapter[0].setDropdownListener(object : AlertRVAdapter.DropdownListener{
            override fun setTime(position: Int) {
                binding.alertHourTv.text = hours[position]
            }
        })
        adapter[1].setDropdownListener(object : AlertRVAdapter.DropdownListener{
            override fun setTime(position: Int) {
                binding.alertMinuteTv.text = minutes[position]
            }
        })
    }
}