package com.example.planup.goal.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.planup.R
import com.example.planup.databinding.FragmentPictureSettingBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.util.backStackTrueGoalNav
import com.example.planup.goal.util.equil
import com.example.planup.goal.util.setInsets
import com.example.planup.goal.util.titleFormat
import com.example.planup.main.goal.viewmodel.GoalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PictureSettingFragment : Fragment() {

    private var _binding: FragmentPictureSettingBinding? = null
    private val binding get() = _binding!!
    private var selectedFrequency: Int = 1

    // SharedPreferences 추가
    private val PREFS_NAME = "goal_data"
    private val KEY_FREQUENCY = "last_frequency"
    private val KEY_VERIFICATION_TYPE = "last_verification_type"

    private val viewModel: GoalViewModel by activityViewModels()

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
//        updateNextButtonUi(false) // 초기 상태: 버튼 비활성화
        setInsets(view)
        setEdit()
    }

    private fun setupListeners() {
        // 뒤로가기 버튼 -> 이전 화면
        binding.backIv.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.challengePhotoNumberTv.setOnClickListener {
            val items = arrayListOf("1", "2", "3")
            showDropdown(items, binding.challengePhotoNumberTv)
        }

        // 다음 버튼 -> GoalDetailFragment로 이동
        binding.challengeTimerNextBtn.setOnClickListener {
                val activity = requireActivity() as GoalActivity
                // GoalActivity에 인증 횟수와 인증 방식 저장
                activity.oneDose = selectedFrequency.toString()
                activity.verificationType = "PICTURE"

                // SharedPreferences에 저장
                val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit()
                    .putInt(KEY_FREQUENCY, selectedFrequency)
                    .putString(KEY_VERIFICATION_TYPE, "PICTURE")
                    .apply()

                val goalDetailFragment = GoalDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString("goalOwnerName", activity.goalOwnerName)
                    }
                }
                backStackTrueGoalNav(goalDetailFragment,"PictureSettingFragment")
        }
    }

    // 드롭다운 표시
    private fun showDropdown(
        items: ArrayList<String>,
        anchor: View
    ) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.popup_challenge_photo,null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(anchor)
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.transparent))
        popupView.findViewById<TextView>(R.id.popup_challenge_photo_once_tv).setOnClickListener {
            binding.challengePhotoNumberTv.setText(R.string.challenge_photo_once)
            selectedFrequency = 1
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.popup_challenge_photo_twice_tv).setOnClickListener {
            binding.challengePhotoNumberTv.setText(R.string.challenge_photo_twice)
            selectedFrequency = 2
            popupWindow.dismiss()
        }
        popupView.findViewById<TextView>(R.id.popup_challenge_photo_three_tv).setOnClickListener {
            binding.challengePhotoNumberTv.setText(R.string.challenge_photo_three)
            selectedFrequency = 3
            popupWindow.dismiss()
        }
    }

    private fun updateNextButtonUi(enabled: Boolean) {
        binding.challengeTimerNextBtn.isEnabled = enabled
        binding.challengeTimerNextBtn.isActivated = enabled
        binding.challengeTimerNextBtn.background = ContextCompat.getDrawable(
            requireContext(),
            if (enabled) R.drawable.btn_next_background else R.drawable.btn_next_background_gray
        )
    }

    override fun onResume() {
        super.onResume()
        setEdit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEdit() {
        val activity = requireActivity() as GoalActivity
        val frequency = binding.challengePhotoNumberTv
        Log.d("PictureSettingFragment", "friendNickname: ${viewModel.friendNickname}")
        val goalDataE = if (viewModel.editGoalData != null){
            viewModel.editGoalData!!.run {
                equil(
                    copy(goalName = activity.goalName, goalAmount = activity.goalAmount,
                        verificationType = activity.verificationType)
                )
            }
        }else false
        titleFormat(activity.isFriendTab,goalDataE, binding.titleTv,
            if (viewModel.friendNickname!="사용자")viewModel.friendNickname else activity.goalOwnerName){

        }
        if(activity.verificationType == "PICTURE" && activity.frequency != 0){
            frequency.text = "${activity.frequency}번"
            updateNextButtonUi(true)
        }
    }
}
