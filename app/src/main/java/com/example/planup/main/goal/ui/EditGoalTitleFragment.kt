package com.example.planup.main.goal.ui

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.planup.R
import com.example.planup.databinding.FragmentEditGoalTitleBinding
import com.example.planup.main.goal.item.EditGoalResponse
import kotlinx.coroutines.launch
import android.text.TextWatcher
import androidx.fragment.app.activityViewModels
import com.example.planup.goal.util.setInsets
import com.example.planup.main.goal.viewmodel.GoalViewModel
import com.example.planup.network.RetrofitInstance
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditGoalTitleFragment : Fragment() {
    private lateinit var binding: FragmentEditGoalTitleBinding

//    private var goalId: Int = 0
////    private var isSolo: Boolean = false
//    private var goalData: EditGoalResponse? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // arguments에서 goalId 받아오기
//        goalId = arguments?.getInt("goalId") ?: 0
//
//        Log.d("EditGoalTitleFragment", "goalId: $goalId")
//
////        isSolo = arguments?.getBoolean("isSolo") ?: false
//    }

    private val viewModel: GoalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditGoalTitleBinding.inflate(inflater, container, false)


        val titleEt = binding.editFriendGoalNameEt
        val goalAmountEt = binding.editFriendGoalPeriodEt
        val nextBtn = binding.editFriendGoalNextBtn
        val backBtn = binding.editFriendBackArrowIv
        backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
//            else requireActivity().finish()
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
            viewModel.setGoalData(
                viewModel.goalData.copy(
                    goalName = goalName,
                    goalAmount = goalAmount,
                )
            )
            Log.d("EditGoalTitleFragment", "goalData: ${viewModel.goalData}")

            val nextFragment = if(viewModel.editGoalData!!.verificationType == "TIMER")EditGoalTimerFragment()
            else EditPictureSettingFragment()
//            nextFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.edit_friend_goal_fragment_container, nextFragment)
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setInsets(binding.root)
        viewModel.setGoalData(
            viewModel.goalData.copy(
                goalName = viewModel.editGoalData?.goalName ?: "",
                goalAmount = viewModel.editGoalData?.goalAmount ?: "",
                goalType = viewModel.editGoalData?.goalType ?: "",
                verificationType = viewModel.editGoalData?.verificationType ?: ""
            )
        )
        Log.d("EditGoalTitleFragment", "goalData: ${viewModel.goalData}")
        viewModel.goalData.apply {
            binding.editFriendGoalNameEt.setText(goalName)
            binding.editFriendGoalPeriodEt.setText(goalAmount)
        }
    }
}
