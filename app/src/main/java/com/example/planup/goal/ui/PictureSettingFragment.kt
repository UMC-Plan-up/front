package com.example.planup.goal.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.planup.R
import com.example.planup.databinding.FragmentPictureSettingBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.adapter.TimerRVAdapter

class PictureSettingFragment : Fragment() {

    private var _binding: FragmentPictureSettingBinding? = null
    private val binding get() = _binding!!
    private var selectedFrequency: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPictureSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        updateNextButtonUi(false) // 초기 상태: 버튼 비활성화
    }

    private fun setupListeners() {
        // 뒤로가기 버튼 -> CertificationMethodFragment로 이동
        binding.backIv.setOnClickListener {
            (requireActivity() as GoalActivity).navigateToFragment(CertificationMethodFragment())
        }

        binding.dropdownContainer.setOnClickListener {
            val items = arrayListOf("1", "2", "3")
            showDropdown(items, binding.dropdownContainer, binding.challengeTimerHourTv)
        }

        // 다음 버튼 -> GoalDetailFragment로 이동
        binding.challengeTimerNextBtn.setOnClickListener {
            if (selectedFrequency != null) {
                val activity = requireActivity() as GoalActivity

                // GoalActivity에 인증 횟수와 인증 방식 저장
                activity.frequency = selectedFrequency!!
                activity.verificationType = "PICTURE"

                val goalDetailFragment = GoalDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("goalOwnerName", activity.goalOwnerName)
                        putString("goalType", activity.goalType)
                        putString("goalCategory", activity.goalCategory)
                        putString("goalName", activity.goalName)
                        putString("goalAmount", activity.goalAmount)
                        putString("verificationType", activity.verificationType)
                        putInt("frequency", activity.frequency)
                    }
                }
                activity.navigateToFragment(goalDetailFragment)
            }
        }
    }

    // 드롭다운 표시
    private fun showDropdown(
        items: ArrayList<String>,
        anchor: View,
        label: TextView
    ) {
        val popupView = layoutInflater.inflate(R.layout.item_recycler_dropdown_time, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.setBackgroundDrawable(
            ContextCompat.getColor(requireContext(), R.color.transparent).toDrawable()
        )
        popupWindow.elevation = 8f
        popupWindow.width = anchor.width

        popupView.findViewById<RecyclerView>(R.id.dropdown_recycler_rv).apply {
            val adapter = TimerRVAdapter(items)
            this.adapter = adapter
            adapter.setDropdownListener(object : TimerRVAdapter.DropdownListener {
                override fun setTime(position: Int) {
                    val selectedText = items[position]
                    label.text = "${selectedText}번"
                    selectedFrequency = selectedText.toIntOrNull()
                    updateNextButtonUi(true)
                    popupWindow.dismiss()
                }
            })
        }

        popupWindow.showAsDropDown(anchor, 0, 0)
    }

    private fun updateNextButtonUi(enabled: Boolean) {
        binding.challengeTimerNextBtn.isEnabled = enabled
        binding.challengeTimerNextBtn.isActivated = enabled
        binding.challengeTimerNextBtn.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.btn_next_background else R.drawable.btn_next_background_gray
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}