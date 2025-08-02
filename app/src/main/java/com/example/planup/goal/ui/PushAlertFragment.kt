package com.example.planup.goal.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.databinding.FragmentPushAlertBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.main.home.ui.HomeFragment

class PushAlertFragment : Fragment() {

    private var shouldShowPopup = false
    private lateinit var binding: FragmentPushAlertBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPushAlertBinding.inflate(inflater, container, false)
        shouldShowPopup = arguments?.getBoolean("SHOW_POPUP") ?: false

//        setSpinner(binding.alertTimeSp, R.array.spinner_morning_afternoon)
//        setSpinner(binding.alertHourSp, R.array.spinner_hour)
//        setSpinner(binding.alertMinuteSp, R.array.spinner_minute_second)

        clickListener()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // BottomSheet Popup 띄우기
        if (shouldShowPopup) {
            shouldShowPopup = false
            showPushAlertPopup()
        }
    }

    /* Push 알림 설정 팝업 띄우기 */
    private fun showPushAlertPopup() {
        val dialog = PushAlertPopupDialog(requireContext())

        dialog.setOnConfirmClickListener {
            // 네 클릭 → popup 닫기
            dialog.dismiss()
        }

        dialog.setOnCancelClickListener {
            // 아니오 클릭 → HomeFragment로 이동
            dialog.dismiss()
            (requireActivity() as GoalActivity)
                .navigateToFragment(HomeFragment())
        }

        dialog.show()
    }

    /* 뒤로가기 & 저장 버튼 이벤트 설정 + 요일 선택 + 토글 */
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
        binding.nextButton.setOnClickListener {
            // TODO: 저장 버튼 클릭 시 Push 알림 설정 저장

            binding.notificationSaveMessage.visibility = View.VISIBLE

            // 2초 후 HomeFragment로 이동
            Handler(Looper.getMainLooper()).postDelayed({
                (requireActivity() as GoalActivity)
                    .navigateToFragment(HomeFragment())
            }, 2000)
        }

        /* 요일 선택 효과 */
        val selected = ContextCompat.getColor(requireContext(), R.color.blue_200)
        val unselected = ContextCompat.getColor(requireContext(), R.color.black_300)

        listOf(
            binding.alertEverydayCl to binding.alertEverydayTv,
            binding.alertMondayCl to binding.alertMondayTv,
            binding.alertTuesdayCl to binding.alertTuesdayTv,
            binding.alertWednesdayCl to binding.alertWednesdayTv,
            binding.alertThursdayCl to binding.alertThursdayTv,
            binding.alertFridayCl to binding.alertFridayTv,
            binding.alertSaturdayCl to binding.alertSaturdayTv,
            binding.alertSundayCl to binding.alertSundayTv,
        ).forEach { (container, textView) ->
            container.setOnClickListener {
                container.isActivated = !container.isActivated
                textView.setTextColor(if (container.isActivated) selected else unselected)
            }
        }

        // 뒤로가기
        binding.backIcon.setOnClickListener {
            (requireActivity() as GoalActivity)
                .navigateToFragment(GoalCompleteFragment())
        }
    }

//    /* 스피너 초기화 */
//    private fun setSpinner(
//        spinnerId: androidx.appcompat.widget.AppCompatSpinner,
//        stringId: Int
//    ) {
//        val items = resources.getStringArray(stringId)
//        val adapter = ArrayAdapter(
//            requireContext(),
//            R.layout.item_spinner_challenge_alert,
//            items
//        )
//        adapter.setDropDownViewResource(R.layout.dropdown_alert)
//        spinnerId.adapter = adapter
//        spinnerId.setSelection(0, false)
//
//        spinnerId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val selected = parent.getItemAtPosition(position).toString()
//                // 선택된 값 사용 가능 (예: Toast 등)
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // 선택 안됨
//            }
//        }
//    }

    companion object {
        fun newInstance(showPopup: Boolean = false): PushAlertFragment {
            val fragment = PushAlertFragment()
            val args = Bundle()
            args.putBoolean("SHOW_POPUP", showPopup)
            fragment.arguments = args
            return fragment
        }
    }
}
