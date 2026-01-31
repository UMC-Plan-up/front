package com.example.planup.goal.ui

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.planup.databinding.FragmentEditGoalTitleBinding
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.example.planup.goal.GoalActivity
import com.example.planup.goal.domain.setGoalData
import com.example.planup.main.goal.viewmodel.GoalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendEditFragment : Fragment() {
    private lateinit var binding: FragmentEditGoalTitleBinding

    private var goalId: Int = 0

    private val viewModel: GoalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // arguments에서 goalId 받아오기
        goalId = arguments?.getInt("goalId") ?: 0
        viewModel.setFriendNickname(arguments?.getString("goalOwnerName") ?: "사용자")

        Log.d("EditGoalTitleFragment", "goalId: $goalId")
        Log.d("EditGoalTitleFragment", "friendNickname: ${arguments?.getString("goalOwnerName")}")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditGoalTitleBinding.inflate(inflater, container, false)


        viewModel.getGoalEditData(
            goalId,
            goalDataAction = {
                setGoalData(it)
            },
            backAction = {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }

        )
        val goalActivity = (requireActivity() as GoalActivity)
        val titleTv = binding.editFriendTitleTv
        val titleDescriptionTv = binding.editFriendTitleDescriptionTv
        val titleEt = binding.editFriendGoalNameEt
        val goalAmountEt = binding.editFriendGoalPeriodEt
        val nextBtn = binding.editFriendGoalNextBtn
        val backBtn = binding.editFriendBackArrowIv
        if(viewModel.friendNickname != "사용자") {
            Log.d("EditGoalTitleFragment", "friendNickname: ${viewModel.friendNickname}")
            titleTv.text = "${viewModel.friendNickname}님의 세부 목표"
            titleDescriptionTv.text = "친구가 설정한 세부목표를 그대로 설정할수 있어요"
            titleEt.setText(goalActivity.goalName)
            goalAmountEt.setText(goalActivity.goalAmount)
        }
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.editFriendGoalNameEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString() ?: ""
                val length = input.length

                when {
                    length == 0 -> {
                        binding.editFriendGoalDescriptionTv.text = "1글자 이상 작성해 주세요."
                        binding.editFriendGoalDescriptionTv.visibility = View.VISIBLE
                    }
                    length > 20 -> {
                        binding.editFriendGoalDescriptionTv.text = "20자 이내로 작성해 주세요."
                        binding.editFriendGoalDescriptionTv.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.editFriendGoalDescriptionTv.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editFriendGoalPeriodEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val input = s?.toString() ?: ""
                val length = input.length

                when {
                    length == 0 -> {
                        binding.editFriendGoalPeriodDescriptionTv.text = "1글자 이상 작성해 주세요."
                        binding.editFriendGoalPeriodDescriptionTv.visibility = View.VISIBLE
                    }
                    length > 30 -> {
                        binding.editFriendGoalPeriodDescriptionTv.text = "30자 이내로 작성해 주세요."
                        binding.editFriendGoalPeriodDescriptionTv.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.editFriendGoalPeriodDescriptionTv.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        nextBtn.setOnClickListener {
            //테스트
            val goalName = titleEt.text.toString()
            val goalAmount = goalAmountEt.text.toString()
            val activity = requireActivity() as GoalActivity
            activity.goalName = goalName
            activity.goalAmount = goalAmount

            val bundle = Bundle().apply {
                putBoolean("friendEditBoolean", true)
            }
            val nextFragment = CertificationMethodFragment()
            nextFragment.arguments = bundle
            (requireActivity() as GoalActivity)
                .navigateToFragment(nextFragment)
//            if(viewModel.friendNickname != "사용자")
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.edit_friend_goal_fragment_container, nextFragment)
//                .addToBackStack(null)
//                .commit()
//            else
//                (requireActivity() as GoalActivity)
//                    .navigateToFragment(nextFragment)

//            goalData?.let { goal ->
//                val goalName = titleEt.text.toString()
//                val goalAmount = goalAmountEt.text.toString()
//
//                val bundle = Bundle().apply {
//                    putInt("goalId", goalId)
//                    putString("goalName", goalName)
//                    putInt("oneDose", goal.oneDose)
//                    if(isSolo) putString("goalCategory", arguments?.getString("selectedCategory"))
//                    else putString("goalCategory", goal.goalCategory)
//                    putString("goalCategory", goal.goalCategory)
//                    putString("goalType", goal.goalType)
//                    putString("period", goal.period)
//                    putString("endDate", goal.endDate)
//                    putString("verificationType", goal.verificationType)
//                    putInt("limitFriendCount", goal.limitFriendCount)
//                    putInt("goalTime", goal.goalTime)
//                    putInt("frequency", goal.frequency)
//                    putString("goalAmount", goalAmount)
//                }
//
//                val nextFragment = EditGoalTimerFragment()
//                nextFragment.arguments = bundle
//
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.edit_friend_goal_fragment_container, nextFragment)
//                    .addToBackStack(null)
//                    .commit()
//            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                systemBars.bottom
            )
            insets
        }
    }
}
