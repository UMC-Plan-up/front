package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.planup.R
import com.example.planup.goal.GoalActivity
import com.example.planup.databinding.FragmentParticipantLimitBinding
import com.example.planup.goal.util.backStackTrueGoalNav
import com.example.planup.goal.util.equil
import com.example.planup.goal.util.setInsets
import com.example.planup.goal.util.titleFormat
import com.example.planup.main.goal.viewmodel.GoalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ParticipantLimitFragment : Fragment() {

    private var _binding: FragmentParticipantLimitBinding? = null
    private val binding get() = _binding!!

    private var isInputValid = false
    private var goalOwnerName: String? = null

    private val viewModel: GoalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentParticipantLimitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInsets(view)
        setupClickListeners()
        setupInputValidation()

        //setInsets(binding.root,)
        view.setOnTouchListener { _, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN &&
                binding.participantLimitEditText.isFocused
            ) {
                binding.participantLimitEditText.clearFocus()
                hideKeyboard()
            }
            view.performClick()
            false
        }

        setEdit()
    }

    private fun setupClickListeners() {
        binding.backIcon.setOnClickListener {
//            val goalDetailFragment = GoalDetailFragment()
//            (activity as? GoalActivity)?.navigateToFragment(goalDetailFragment)
//                ?: parentFragmentManager.popBackStack()
            parentFragmentManager.popBackStack()
        }

        binding.nextButton.setOnClickListener {
            if (isInputValid) {
                val limit = binding.participantLimitEditText.text.toString().toIntOrNull() ?: 0
                val ga = activity as? GoalActivity

                if (ga != null) {
                    ga.limitFriendCount = limit
                    val pushAlertCommunityFragment = PushAlertCommunityFragment().apply {
                        arguments = Bundle().apply {
                            putString("goalOwnerName", ga.goalOwnerName)
                            putString("goalType", ga.goalType)
                            putString("goalCategory", ga.goalCategory)
                            putString("goalName", ga.goalName)
                            putString("goalAmount", ga.goalAmount)
                            putString("verificationType", ga.verificationType)
                            putString("period", ga.period)
                            putInt("frequency", ga.frequency)
                            putInt("limitFriendCount", ga.limitFriendCount)
                        }
                    }
//                    ga.navigateToFragment(pushAlertCommunityFragment)
                    backStackTrueGoalNav(pushAlertCommunityFragment,"ParticipantLimitFragment")
                } else {
                    val pushAlertCommunityFragment = PushAlertCommunityFragment().apply {
                        arguments = Bundle().apply {
                            putString("goalOwnerName", goalOwnerName)
                            putInt("limitFriendCount", limit)
                        }
                    }
                    val containerId = (view?.parent as? ViewGroup)?.id
                    if (containerId != null && containerId != View.NO_ID) {
//                        backStackTrueNav(containerId,pushAlertCommunityFragment,"ParticipantLimitFragment")
                        parentFragmentManager.beginTransaction()
                            .replace(containerId, pushAlertCommunityFragment)
                            .addToBackStack(null)
                            .commit()
                    } else {
//                        backStackTrueNav(android.R.id.content,pushAlertCommunityFragment,"ParticipantLimitFragment")
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(android.R.id.content, pushAlertCommunityFragment)
                            .addToBackStack(null)
                            .commitAllowingStateLoss()
                    }
                }
            }
        }
    }

    /* 참여자 제한 인원 입력 조건 검증 */
    private fun setupInputValidation() {
        // 숫자만 입력 가능
        binding.participantLimitEditText.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[0-9]*"))) source else ""
        })

        binding.participantLimitEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString().toIntOrNull() ?: 0

                if (value < 1) {
                    // 1 미만 → 오류 메시지 표시
                    binding.participantLimitErrorText.visibility = View.VISIBLE
                    disableNextButton()
                    isInputValid = false
                } else {
                    // 1 이상 → 오류 메시지 숨김
                    binding.participantLimitErrorText.visibility = View.GONE
                    enableNextButton()
                    isInputValid = true
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    /* 버튼 비활성화 */
    private fun disableNextButton() {
        binding.nextButton.isEnabled = false
        binding.nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background_gray)
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /* 버튼 활성화 */
    private fun enableNextButton() {
        binding.nextButton.isEnabled = true
        binding.nextButton.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.btn_next_background)
    }

    override fun onResume() {
        super.onResume()
        setEdit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEdit(){
        val activity = requireActivity() as GoalActivity
        val goalDataE = if (viewModel.editGoalData != null){
            viewModel.editGoalData!!.run {
                val data = copy(goalName = activity.goalName, goalAmount = activity.goalAmount,
                    verificationType = activity.verificationType, period = activity.period,
                    endDate = if (activity.endDate != null)activity.endDate!! else "",
                    frequency = activity.frequency)
                equil(
                    when(data.verificationType){
                        "PICTURE" -> data.copy(oneDose = activity.oneDose.toIntOrNull() ?: 0)
                        else -> data.copy(goalTime = activity.goalTime)
                    })
            }
        }else false
        titleFormat(activity.isFriendTab,goalDataE, binding.friendGoalTitleText,
            if (viewModel.friendNickname != "사용자")viewModel.friendNickname
                else activity.goalOwnerName){
            binding.participantLimitErrorText.visibility = View.GONE
            /* 처음엔 다음 버튼 비활성화 */
            disableNextButton()
        }

        if (activity.limitFriendCount != 0) {
            binding.participantLimitEditText.setText(activity.limitFriendCount.toString())
            isInputValid = true
        } else {
            /* 처음엔 다음 버튼 비활성화 */
            disableNextButton()
        }

    }

}