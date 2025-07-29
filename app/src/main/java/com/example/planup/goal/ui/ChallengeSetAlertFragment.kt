package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentChallengeSetAlertBinding
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
        setSpinner(binding.alertTimeSp,R.array.spinner_morning_afternoon)
        setSpinner(binding.alertHourSp,R.array.spinner_hour)
        setSpinner(binding.alertMinuteSp,R.array.spinner_minute_second)
        clickListener()
        return binding.root
    }

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

        /* 요일 선택 효과 */
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

    private fun setSpinner (spinnerId:AppCompatSpinner, stringId: Int) {
        val spinner = spinnerId
        val items = resources.getStringArray(stringId)
        val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner_challenge_alert, items)//스피너 위젯 설정
        adapter.setDropDownViewResource(R.layout.dropdown_alert)//드롭다운 메뉴 설정
        spinner.adapter = adapter

        // spinner에 띄울 기본값 설정 (0번째)
        spinner.setSelection(0, false)

        // 리스너 연결
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected = parent.getItemAtPosition(position).toString()
                // 선택된 값(selected)을 필요에 맞게 사용 (예: TextView에 반영 등)
                // 예시 Toast 표시
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무것도 선택하지 않았을 때 동작 (선택사항)
            }
        }
    }
}