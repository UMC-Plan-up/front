package com.planup.planup.goal.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.planup.planup.R
import com.planup.planup.databinding.FragmentTimerSettingBinding
import com.planup.planup.goal.GoalActivity
import com.planup.planup.goal.adapter.TimerRVAdapter
import com.planup.planup.goal.util.backStackTrueGoalNav
import com.planup.planup.goal.util.clockString
import com.planup.planup.goal.util.equil
import com.planup.planup.goal.util.setInsets
import com.planup.planup.goal.util.titleFormat
import com.planup.planup.main.goal.viewmodel.GoalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerSettingFragment : Fragment() {
    lateinit var binding: FragmentTimerSettingBinding
    private var totalTime = 0
    lateinit var hours: ArrayList<String> // 시
    lateinit var minutes: ArrayList<String> // 분
    lateinit var seconds: ArrayList<String> // 초

    // SharedPreferences 추가
    private val PREFS_NAME = "goal_data"
    private val KEY_GOAL_TIME = "last_goal_time"
    private val KEY_VERIFICATION_TYPE = "last_verification_type"

    private val viewModel: GoalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerSettingBinding.inflate(inflater, container, false)
        init()
        clickListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets(binding.root)
        setEdit()
    }

    // 프레그먼트 초기화
    private fun init() {
        hours = resources.getStringArray(R.array.dropdown_hour).toCollection(ArrayList<String>())
        minutes = resources.getStringArray(R.array.dropdown_minute_second)
            .toCollection(ArrayList<String>())
        seconds = resources.getStringArray(R.array.dropdown_minute_second)
            .toCollection(ArrayList<String>())
    }

    private fun clickListener() {
        // 이전 버튼 -> 인증방식 설정 페이지로 이동
        binding.backIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // 타이머 시간 설정
        binding.challengeTimerHourTv.setOnClickListener { // 시간
            showDropdown(hours, binding.challengeTimerHourTv, 0)
        }
        binding.challengeTimerMinuteTv.setOnClickListener { // 분
            showDropdown(minutes, binding.challengeTimerMinuteTv, 1)
        }
        binding.challengeTimerSecondTv.setOnClickListener { // 초
            showDropdown(seconds, binding.challengeTimerSecondTv, 2)
        }

        // 다음 버튼 -> GoalDetailFragment로 이동
        binding.challengeTimerNextBtn.setOnClickListener {
            if (!binding.challengeTimerNextBtn.isActivated) {
                return@setOnClickListener
            }

            val activity = requireActivity() as GoalActivity
            activity.goalTime = totalTime // 타이머 총 시간 저장(초)
            activity.verificationType = "TIMER" // 인증 방식 저장

            // SharedPreferences에 저장
            val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putInt(KEY_GOAL_TIME, totalTime)
                .putString(KEY_VERIFICATION_TYPE, "TIMER")
                .apply()

            val goalDetailFragment = GoalDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", activity.goalOwnerName)
                    putString("SELECTED_METHOD", "TIMER")
                }
            }
            backStackTrueGoalNav(goalDetailFragment,"TimerSettingFragment")
        }
    }

    // 타이머로 설정한 시간 업데이트
    private fun timeWatcher(item: Int, position: Int) {
        // 기존 총 시간에서 해당 부분 시간을 빼고 새로운 시간을 더함
        val hour = (totalTime / 3600) * 3600
        val minute = ((totalTime - (totalTime / 3600) * 3600) / 60) * 60
        val second =
            totalTime - (totalTime / 3600) * 3600 - ((totalTime - (totalTime / 3600) * 3600) / 60) * 60

        if (position == 0) {
            totalTime -= hour
            totalTime += 3600 * item
        } else if (position == 1) {
            totalTime -= minute
            totalTime += 60 * item
        } else if (position == 2) {
            totalTime -= second
            totalTime += item
        }

        /* 다음 버튼 활성화 조건 */
        if (totalTime < 30) {
            binding.errorTv.visibility = View.VISIBLE
            binding.challengeTimerNextBtn.isActivated = false
        } else {
            binding.errorTv.visibility = View.GONE
            binding.challengeTimerNextBtn.isActivated = true
        }
    }

    // 버튼 활성/비활성
    private fun updateNextButtonUi(enabled: Boolean) {
        binding.challengeTimerNextBtn.isEnabled = enabled
        binding.challengeTimerNextBtn.isActivated = enabled
        val bg = if (enabled) {
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        } else {
            // 버튼 비활성
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background_gray)
        }
        binding.challengeTimerNextBtn.background = bg
    }

//    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()
    //시간, 분, 초 드롭다운
    private fun showDropdown(
        items: ArrayList<String>, //시간
        view: TextView, //앵커뷰
        selected: Int //시, 분, 초 중 어느 드롭다운인지 표시
    ) {//리사이클러 뷰 아이템, 앵커 뷰, 시/분/초
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.item_recycler_dropdown_time, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        val dropdownAdapter = TimerRVAdapter(items)
        popupWindow.showAsDropDown(view) //선택된 뷰 하단에 드롭다운 표시
        popupWindow.isOutsideTouchable = true //바깥 터치 허용
        popupWindow.setBackgroundDrawable(
            resources.getColor(R.color.transparent).toDrawable()
        )//투명 배경 설정

        //드롭다운 터치 이벤트 관리하는 어댑터
        popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).adapter = dropdownAdapter
        dropdownAdapter.setDropdownListener(object : TimerRVAdapter.DropdownListener {
            override fun setTime(position: Int) {
                when(selected){
                    0 -> view.text = getString(R.string.timer_hour, items[position])
                    1 -> view.text = getString(R.string.timer_minute,items[position])
                    2 -> view.text = getString(R.string.timer_second,items[position])
                }
                timeWatcher(items[position].toInt(), selected)
                popupWindow.dismiss()
            }
        })
    }

//    private fun showDropdown(
//        items: ArrayList<String>,
//        view: TextView,
//        selected: Int
//    ) {
//        val inflater = LayoutInflater.from(context)
//        val popupBinding = ItemRecyclerDropdownTimeBinding.inflate(inflater)
//
//        view.post {
//            val fallbackWidth = when (selected) {
//                0 -> 90.dp()  // 시
//                1 -> 70.dp()  // 분
//                else -> 70.dp() // 초
//            }
//            val exactWidth = if (view.width > 0) view.width else fallbackWidth
//
//            // 팝업 생성
//            val popupWindow = PopupWindow(
//                popupBinding.root,
//                exactWidth,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                true
//            ).apply {
//                isOutsideTouchable = true
//                setBackgroundDrawable(
//                    ContextCompat.getColor(requireContext(), R.color.transparent).toDrawable()
//                )
//                elevation = 8f
//            }
//
//            popupBinding.dropdownRecyclerRv.layoutParams =
//                popupBinding.dropdownRecyclerRv.layoutParams.apply {
//                    width = ViewGroup.LayoutParams.MATCH_PARENT
//                }
//            popupBinding.dropdownRecyclerRv.adapter = TimerRVAdapter(items).apply {
//                setDropdownListener(object : TimerRVAdapter.DropdownListener {
//                    override fun setTime(position: Int) {
//                        val raw = items[position]
//                        val labeled = when (selected) {
//                            0 -> "${raw}시간"
//                            1 -> "${raw}분"
//                            else -> "${raw}초"
//                        }
//                        view.text = labeled
//                        timeWatcher(raw.toInt(), selected)
//                        popupWindow.dismiss()
//                    }
//                })
//            }
//
//            popupWindow.showAsDropDown(view, 0, 0)
//        }
//    }

    private fun setEdit(){
        Log.d("CertificationMethodFragment", "friendNickname: ${viewModel.friendNickname}")
        val activity = requireActivity() as GoalActivity
        val goalDataE = if (viewModel.editGoalData != null){
            viewModel.editGoalData!!.run {
                val data = copy(goalName = activity.goalName, goalAmount = activity.goalAmount,
                    verificationType = activity.verificationType)
                Log.d("TimerSettingFragment", "goalData: $this")
                Log.d("TimerSettingFragment", "activity goalData: $data")
                equil(
                    data
                )
            }
        }else false
        titleFormat(activity.isFriendTab,goalDataE, binding.titleTv,
            if (viewModel.friendNickname!="사용자")viewModel.friendNickname else activity.goalOwnerName){

        }
        if(activity.verificationType == "TIMER"){
            Log.d("TimerSettingFragment", "goalData: ${viewModel.editGoalData}")
            activity.let {
                if (it.goalTime >29) {
                    val time = it.goalTime
                    Log.d("TimerSettingFragment", "time: $time")
                    totalTime = it.goalTime
                    // 기존 총 시간에서 해당 부분 시간을 빼고 새로운 시간을 더함
                    val hour = time / 3600
                    val minute = (time % 3600) / 60
                    val second = time % 60
                    binding.challengeTimerHourTv.text =
                        getString(R.string.timer_hour, hour.clockString())
                    binding.challengeTimerMinuteTv.text =
                        getString(R.string.timer_minute, minute.clockString())
                    binding.challengeTimerSecondTv.text =
                        getString(R.string.timer_second, second.clockString())

                    updateNextButtonUi(true)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setEdit()
    }
}
