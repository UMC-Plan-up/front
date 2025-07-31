package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.example.planup.R
import com.example.planup.goal.GoalActivity

class TimerSettingFragment : Fragment() {

    private lateinit var hourEditText: EditText
    private lateinit var minuteEditText: EditText
    private lateinit var secondEditText: EditText

    private lateinit var nextButton: AppCompatButton
    private lateinit var backIcon: ImageView

    private var goalOwnerName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer_setting, container, false)

        goalOwnerName = arguments?.getString("goalOwnerName")
            ?: throw IllegalStateException("TimerSettingFragment must receive goalOwnerName!")

        initViews(view)
        setupNextButton()
        setupBackButton()

        return view
    }

    /* 뷰 초기화 */
    private fun initViews(view: View) {
        hourEditText = view.findViewById(R.id.hourEditText)
        minuteEditText = view.findViewById(R.id.minuteEditText)
        secondEditText = view.findViewById(R.id.secondEditText)

        nextButton = view.findViewById(R.id.nextButton)
        backIcon = view.findViewById(R.id.backIcon)

        // 버튼은 항상 활성화
        nextButton.isEnabled = true
        nextButton.setBackgroundResource(R.drawable.btn_next_background)
    }

    /* 다음 버튼 클릭 → GoalDetailFragment로 이동 */
    private fun setupNextButton() {
        nextButton.setOnClickListener {
            val goalDetailFragment = GoalDetailFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                    putString("SELECTED_METHOD", "TIMER")
                }
            }
            (requireActivity() as GoalActivity).navigateToFragment(goalDetailFragment)
        }
    }

    /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
    private fun setupBackButton() {
        backIcon.setOnClickListener {
            val certFragment = CertificationMethodFragment().apply {
                arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                }
            }
            (requireActivity() as GoalActivity).navigateToFragment(certFragment)
        }
    }
}