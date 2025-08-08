package com.example.planup.main.my.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.main.MainActivity
import com.example.planup.R
import com.example.planup.databinding.FragmentMypageAlertBinding
import com.example.planup.main.my.adapter.AlertRVAdapter

class MypageAlertFragment : Fragment() {
    lateinit var binding: FragmentMypageAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageAlertBinding.inflate(inflater, container, false)
        showDropdown()
        clickListener()
        return binding.root
    }

    //클릭 이벤트
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
            binding.serviceTimeCl.visibility = View.GONE
            binding.serviceHourRv.visibility = View.GONE
            binding.serviceMinuteRv.visibility = View.GONE
        }
        binding.serviceRegularOffIv.setOnClickListener {
            binding.serviceRegularOnIv.visibility = View.VISIBLE
            binding.serviceRegularOffIv.visibility = View.GONE
            binding.serviceDayCl.visibility = View.VISIBLE
        }

        /* 시간 설정 */
        //오전, 오후
        binding.serviceTimeTv.setOnClickListener {
            binding.serviceTimeCl.visibility = View.VISIBLE
        }
        binding.serviceMorningTv.setOnClickListener {
            binding.serviceTimeTv.text = binding.serviceMorningTv.text.toString()
            binding.serviceTimeCl.visibility = View.GONE
        }
        binding.serviceAfternoonTv.setOnClickListener {
            binding.serviceTimeTv.text = binding.serviceAfternoonTv.text.toString()
            binding.serviceTimeCl.visibility = View.GONE
        }
        //시각
        binding.serviceHourTv.setOnClickListener {
            binding.serviceHourRv.visibility = View.VISIBLE
        }
        //분
        binding.serviceMinuteTv.setOnClickListener {
            binding.serviceMinuteRv.visibility = View.VISIBLE
        }

        /* 요일 선택 효과 */
        val selected = ContextCompat.getColor(context, R.color.blue_200)
        val unselected = ContextCompat.getColor(context, R.color.black_300)
        // 매일
        binding.serviceEverydayTv.setOnClickListener {
            binding.serviceEverydayTv.isSelected = !binding.serviceEverydayTv.isSelected
            binding.serviceEverydayTv.setTextColor(
                if (binding.serviceEverydayTv.isSelected) selected else unselected
            )
        }

        // 월요일
        binding.serviceMondayTv.setOnClickListener {
            binding.serviceMondayTv.isSelected = !binding.serviceMondayTv.isSelected
            binding.serviceMondayTv.setTextColor(
                if (binding.serviceMondayTv.isSelected) selected else unselected
            )
        }

        // 화요일
        binding.serviceTuesdayTv.setOnClickListener {
            binding.serviceTuesdayTv.isSelected = !binding.serviceTuesdayTv.isSelected
            binding.serviceTuesdayTv.setTextColor(
                if (binding.serviceTuesdayTv.isSelected) selected else unselected
            )
        }

        // 수요일
        binding.serviceWednesdayTv.setOnClickListener {
            binding.serviceWednesdayTv.isSelected = !binding.serviceWednesdayTv.isSelected
            binding.serviceWednesdayTv.setTextColor(
                if (binding.serviceWednesdayTv.isSelected) selected else unselected
            )
        }

        // 목요일
        binding.serviceThursdayTv.setOnClickListener {
            binding.serviceThursdayTv.isSelected = !binding.serviceThursdayTv.isSelected
            binding.serviceThursdayTv.setTextColor(
                if (binding.serviceThursdayTv.isSelected) selected else unselected
            )
        }

        // 금요일
        binding.serviceFridayTv.setOnClickListener {
            binding.serviceFridayTv.isSelected = !binding.serviceFridayTv.isSelected
            binding.serviceFridayTv.setTextColor(
                if (binding.serviceFridayTv.isSelected) selected else unselected
            )
        }

        // 토요일
        binding.serviceSaturdayTv.setOnClickListener {
            binding.serviceSaturdayTv.isSelected = !binding.serviceSaturdayTv.isSelected
            binding.serviceSaturdayTv.setTextColor(
                if (binding.serviceSaturdayTv.isSelected) selected else unselected
            )
        }

        // 일요일
        binding.serviceSundayTv.setOnClickListener {
            binding.serviceSundayTv.isSelected = !binding.serviceSundayTv.isSelected
            binding.serviceSundayTv.setTextColor(
                if (binding.serviceSundayTv.isSelected) selected else unselected
            )
        }



    }
    //알림설정 드롭다운 시간/분
    private fun showDropdown(){
        //시간, 분 어레이 생성
        val hours:ArrayList<String> = resources.getStringArray(R.array.dropdown_hour).toCollection(ArrayList())
        val minutes:ArrayList<String> = resources.getStringArray(R.array.dropdown_minute_second).toCollection(ArrayList())
        //리사이클러 뷰 어댑터 설정
        val adapter = ArrayList<AlertRVAdapter>()
        adapter.add(0, AlertRVAdapter(hours))
        adapter.add(1, AlertRVAdapter(minutes))

        binding.serviceHourRv.adapter = adapter[0]
        binding.serviceMinuteRv.adapter = adapter[1]

        //드롭다운을 통한 시간 설정
        adapter[0].setDropdownListener(object : AlertRVAdapter.DropdownListener{
            override fun setTime(position: Int) {
                binding.serviceHourTv.text = hours[position]
                binding.serviceHourRv.visibility = View.GONE
            }
        })
        //드롭다운을 통한 분 설정
        adapter[1].setDropdownListener(object : AlertRVAdapter.DropdownListener{
            override fun setTime(position: Int) {
                binding.serviceMinuteTv.text = minutes[position]
                binding.serviceMinuteRv.visibility = View.GONE
            }
        })
    }
}