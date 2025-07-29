package com.example.planup.goal.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalSettingActivity

class PushAlertFragment : Fragment(R.layout.fragment_push_alert) {

    companion object {
        fun newInstance(showPopup: Boolean = false): PushAlertFragment {
            val fragment = PushAlertFragment()
            val args = Bundle()
            args.putBoolean("SHOW_POPUP", showPopup)
            fragment.arguments = args
            return fragment
        }
    }

    private var shouldShowPopup = false

    private lateinit var backIcon: ImageView
    private lateinit var saveButton: AppCompatButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 팝업 표시 여부 확인
        shouldShowPopup = arguments?.getBoolean("SHOW_POPUP") ?: false

        // UI 초기화
        backIcon = view.findViewById(R.id.backIcon)
        saveButton = view.findViewById(R.id.saveButton)

        setupClickListeners()
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
            // 아니오 클릭 → TODO: 나중에 다른 화면 이동
            dialog.dismiss()
        }

        dialog.show()
    }

    /* 뒤로가기 & 저장 버튼 이벤트 설정 */
    private fun setupClickListeners() {
        backIcon.setOnClickListener {
            // 뒤로가기 → GoalCompleteFragment로 이동
            (requireActivity() as GoalSettingActivity)
                .navigateToFragment(GoalCompleteFragment())
        }

        saveButton.setOnClickListener {
            // TODO: 저장 버튼 클릭 시 Push 알림 설정 저장
        }
    }
}

