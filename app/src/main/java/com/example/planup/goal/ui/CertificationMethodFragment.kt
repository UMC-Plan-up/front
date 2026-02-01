package com.example.planup.goal.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.planup.R
import com.example.planup.databinding.FragmentCertificationMethodBinding
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.util.setInsets
import com.example.planup.main.goal.viewmodel.GoalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class CertificationMethodFragment : Fragment() {

    private var _binding: FragmentCertificationMethodBinding? = null
    private val binding get() = _binding!!

    private var selectedMethod: String? = null // "TIMER" or "PICTURE"
    private lateinit var goalOwnerName: String

    // SharedPreferences 추가
    private val PREFS_NAME = "goal_data"
    private val KEY_VERIFICATION_TYPE = "last_verification_type"

    private val viewModel: GoalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCertificationMethodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets(binding.root,binding.root.paddingBottom)
        val activity = requireActivity() as GoalActivity
        goalOwnerName = activity.goalOwnerName
        val titleTv = binding.goalDetailTitle
        Log.d("CertificationMethodFragment", "friendNickname: ${viewModel.friendNickname}")
        if(viewModel.friendNickname != "사용자") {
            Log.d("CertificationMethodFragment", "friendNickname: ${viewModel.friendNickname}")
            Log.d("CertificationMethodFragment", "titleTv: ${getString(R.string.goal_friend_detail, viewModel.friendNickname)}")
            titleTv.text = getString(R.string.goal_friend_detail, viewModel.friendNickname)
            when(activity.verificationType){
                "TIMER" -> selectAuthMethod("TIMER")
                "PICTURE" -> selectAuthMethod("PICTURE")
                else -> {
                    // 다음 버튼 비활성화
                    setNextButtonEnabled(false)
                }
            }
        }
//        binding.goalDetailTitle.text = getString(R.string.goal_friend_detail, goalOwnerName)



        /* 뒤로가기 아이콘 → 이전 화면으로 이동 */
        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /* 타이머 인증 선택 */
        binding.timerAuthLayout.setOnClickListener {
            selectAuthMethod("TIMER")
        }

        /* 사진 인증 선택 */
        binding.pictureAuthLayout.setOnClickListener {
            selectAuthMethod("PICTURE")
        }

        /* 다음 버튼 클릭 시 */
        binding.nextButton.setOnClickListener {
            selectedMethod?.let { method ->
                // GoalActivity에 선택된 인증 방식 저장
                activity.verificationType = method

                // SharedPreferences에도 저장
                val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_VERIFICATION_TYPE, method).apply()

                // 다음 프래그먼트 선택
                val nextFragment = when (method) {
                    "TIMER" -> TimerSettingFragment()
                    else -> PictureSettingFragment()
                }

                nextFragment.arguments = Bundle().apply {
                    putString("goalOwnerName", goalOwnerName)
                }

                // 다음 프래그먼트로 이동
                activity.navigateToFragment(nextFragment)
            }
        }
    }

    /* 인증 방식 선택 처리 */
    private fun selectAuthMethod(method: String) {
        selectedMethod = method
        resetAuthLayoutStyles()

        // 선택된 항목
        when (method) {
            "TIMER" -> binding.timerAuthLayout.setBackgroundResource(R.drawable.bg_picture_selected_blue_stroke)
            "PICTURE" -> binding.pictureAuthLayout.setBackgroundResource(R.drawable.bg_picture_selected_blue_stroke)
        }

        // 인증 방식 선택 후 → 다음 버튼 활성화
        setNextButtonEnabled(true)
    }

    /* 인증 레이아웃 스타일 기본값으로 초기화 */
    private fun resetAuthLayoutStyles() {
        val defaultDrawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.bg_picture_selected
        )
        // 기본 배경/테두리로 복원
        binding.timerAuthLayout.background = defaultDrawable
        binding.pictureAuthLayout.background = defaultDrawable

        binding.timerAuthLayout.backgroundTintList = null
        binding.pictureAuthLayout.backgroundTintList = null
    }

    /* 다음 버튼 활성/비활성 처리 */
    private fun setNextButtonEnabled(enabled: Boolean) {
        binding.nextButton.isEnabled = enabled
        binding.nextButton.background = if (enabled) {
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background_gray)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
